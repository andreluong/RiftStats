package com.riftstats.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riftstats.configs.RabbitMQConfig;
import com.riftstats.dtos.*;
import com.riftstats.enums.LeagueTier;
import com.riftstats.enums.RegionCode;
import com.riftstats.enums.RegionGroup;
import com.riftstats.clients.LeagueApiClient;
import com.riftstats.clients.MatchApiClient;
import com.riftstats.clients.LeagueApiClientFactory;
import com.riftstats.clients.MatchApiClientFactory;
import feign.Response;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import static com.riftstats.utils.Constants.*;
import static com.riftstats.utils.RiotRequestUtils.fetchWithRetries;

@Slf4j
@Service
public class RiotService {
    private static final String PUUID_KEY = "puuid";
    private static final Duration DEFAULT_FETCH_INTERVAL = Duration.ofDays(1);
    private final String riotApiKey;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;
    private final LeagueApiClientFactory leagueApiClientFactory;
    private final MatchApiClientFactory matchApiClientFactory;
    private LeagueApiClient leagueApiClient;
    private MatchApiClient matchApiClient;
    private ScheduledFuture<?> scheduledTask;
    private final TaskScheduler taskScheduler;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JavaType matchIdListType = objectMapper.getTypeFactory()
            .constructCollectionType(ArrayList.class, String.class);

    @Autowired
    public RiotService(@Value("${riot.api}") String riotApiKey,
                       RabbitTemplate rabbitTemplate,
                       RabbitMQConfig rabbitMQConfig,
                       LeagueApiClientFactory leagueApiClientFactory,
                       MatchApiClientFactory matchApiClientFactory,
                       TaskScheduler taskScheduler) {
        if (riotApiKey == null || riotApiKey.isBlank()) {
            String msg = "Riot API key is missing.";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.riotApiKey = riotApiKey;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQConfig = rabbitMQConfig;
        this.leagueApiClientFactory = leagueApiClientFactory;
        this.matchApiClientFactory = matchApiClientFactory;
        this.taskScheduler = taskScheduler;
    }

    @PostConstruct
    private void init() {
        scheduleNextExecution(Duration.ofMillis(0));
    }

    @PreDestroy
    public void cleanup() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
    }

    private void fetchAllRegionalChallengerLeagues() {
        for (RegionGroup group : RegionGroup.values()) {
            for (RegionCode code : group.getRegionCodes()) {
                matchApiClient = matchApiClientFactory.getClientForRegion(group);
                leagueApiClient = leagueApiClientFactory.getClientForRegion(code);
                fetchChallengerLeague(group, code);
            }
        }
        // Schedule next fetch execution
        ZonedDateTime nextExecution = ZonedDateTime.now().plus(DEFAULT_FETCH_INTERVAL);
        log.warn("Finished current fetching task. Next scheduled task begins at: {}", nextExecution.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        scheduleNextExecution(DEFAULT_FETCH_INTERVAL);
    }

    // Fetch list of match IDs from player
    private void fetchMatchIds(String puuid, RegionGroup regionGroup, RegionCode regionCode) {
        Optional<Response.Body> body = fetchWithRetries(PUUID_KEY, puuid, id -> matchApiClient.getMatchList(id, riotApiKey));
        if (body.isEmpty()) {
            log.error("Failed to fetch match IDs for puuid={}. Response body is empty.", puuid);
            return;
        }

        try {
            List<String> matchIds = objectMapper.readValue(body.get().asInputStream(), matchIdListType);
            sendToPlayerQueue(objectMapper.writeValueAsString(matchIds), regionGroup, regionCode);
        } catch (Exception e) {
            log.error("Error fetching match IDs for puuid={}: {}", puuid, e.getMessage());
        }
    }

    // Fetch all challenger players from a region and their last 20 matches
    // Regions can have a different amount of challenger players (50-300)
    // Amount can also be 0 if there aren't any challenger players (due to rank reset, etc)
    // NOTE: Limit of 1,000,000 messages in CloudAQMP Lemur
    private void fetchChallengerLeague(RegionGroup regionGroup, RegionCode regionCode) {
        LeagueDTO leagueList = leagueApiClient.getChallengerList(riotApiKey);
        List<PlayerDTO> playerDTOS = leagueList.getEntries();
        log.info("Fetching {}: {} {}; count: {}", regionCode, leagueList.getTier(), leagueList.getQueue(), playerDTOS.size());
        int limitedSize = Math.min(playerDTOS.size(), regionCode.getLeagueSize(LeagueTier.CHALLENGER));
        playerDTOS.subList(0, limitedSize).forEach(player -> fetchMatchIds(player.getPuuid(), regionGroup, regionCode));
    }

    private void sendToPlayerQueue(String message, RegionGroup regionGroup, RegionCode regionCode) {
        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getPlayerExchangeName(),
                rabbitMQConfig.getPlayerRoutingKey(),
                message,
                msg -> {
                    msg.getMessageProperties()
                            .setHeaders(
                                    Map.of(REGION_GROUP_HEADER, regionGroup.getValue(),
                                            REGION_CODE_HEADER, regionCode.getValue())
                            );
                    return msg;
                }
        );
    }

    private void scheduleNextExecution(Duration delay) {
        Instant executionTime = Instant.now().plus(delay);
        scheduledTask = taskScheduler.schedule(this::fetchAllRegionalChallengerLeagues, executionTime);
    }
}
