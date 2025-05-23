package com.riftstats.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riftstats.configs.RabbitMQConfig;
import com.riftstats.dtos.MatchDTO;
import com.riftstats.enums.RegionCode;
import com.riftstats.enums.RegionGroup;
import com.riftstats.clients.MatchApiClient;
import com.riftstats.clients.MatchApiClientFactory;
import com.riftstats.models.Match;
import com.riftstats.repositories.MatchRepository;
import feign.Response;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.riftstats.utils.Constants.*;
import static com.riftstats.utils.RiotRequestUtils.fetchWithRetries;

@Slf4j
@Service
public class MatchFetchService {
    private static final String MATCH_ID_KEY = "matchId";

    private final String riotApiKey;

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMQConfig rabbitMQConfig;

    private final MatchApiClientFactory matchApiClientFactory;

    private MatchApiClient matchApiClient;

    private final MatchRepository matchRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MatchFetchService(@Value("${riot.api.key}") String riotApiKey,
                             RabbitTemplate rabbitTemplate,
                             RabbitMQConfig rabbitMQConfig,
                             MatchApiClientFactory matchApiClientFactory,
                             MatchRepository matchRepository) {
        this.riotApiKey = riotApiKey;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQConfig = rabbitMQConfig;
        this.matchApiClientFactory = matchApiClientFactory;
        this.matchRepository = matchRepository;
    }

    private void sendToMatchQueue(String message, RegionGroup regionGroup, RegionCode regionCode) {
        String routingKey = rabbitMQConfig.getMatchQueueConfig()
                .getRoutingKeys()
                .get(regionGroup.getValue());

        // Sends to region-specific queue
        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getMatchExchangeName(),
                routingKey,
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

    // Fetch match data from match ID and send to queue
    private void fetchMatchData(String matchId, RegionGroup regionGroup, RegionCode regionCode) {
        Optional<Response.Body> body = fetchWithRetries(MATCH_ID_KEY, matchId, id -> matchApiClient.getMatch(id, riotApiKey));
        if (body.isEmpty()) {
            log.error("Failed to fetch match data for matchId={}", matchId);
            return;
        }

        try {
            MatchDTO match = objectMapper.readValue(
                    new BufferedReader(new InputStreamReader(body.get().asInputStream(), StandardCharsets.UTF_8)),
                    MatchDTO.class
            );
            sendToMatchQueue(objectMapper.writeValueAsString(match), regionGroup, regionCode);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @Transactional
    public void fetchMatches(List<String> matchIds, RegionGroup regionGroup, RegionCode regionCode) {
        matchApiClient = matchApiClientFactory.getClientForRegion(regionGroup);

        // Extract existing matchIds from database
        List<Match> existingMatches = matchRepository.findByMatchIdIn(matchIds);
        Set<String> existingMatchIds = existingMatches.stream()
                .map(Match::getMatchId)
                .collect(Collectors.toSet());

        List<String> newMatchIds = matchIds.stream()
                .filter(matchId -> !existingMatchIds.contains(matchId))
                .toList();

        log.info("Processing {} new matchIds: {}", newMatchIds.size(), newMatchIds);
        newMatchIds.forEach(matchId -> fetchMatchData(matchId, regionGroup, regionCode));
    }
}
