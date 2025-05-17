package com.riftstats.services;

import com.riftstats.dtos.*;
import com.riftstats.enums.RegionCode;
import com.riftstats.models.*;
import com.riftstats.repositories.ChampionRepository;
import com.riftstats.repositories.GameVersionRepository;
import com.riftstats.repositories.MatchRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchProcessingService {
    private static final int NO_CHAMPION_BAN_ID = -1; // When no champion ban is chosen in game lobby
    private final MatchRepository matchRepository;
    private final ChampionRepository championRepository;
    private final GameVersionRepository gameVersionRepository;

    @Autowired
    public MatchProcessingService(MatchRepository matchRepository,
                                  ChampionRepository championRepository,
                                  GameVersionRepository gameVersionRepository) {
        this.matchRepository = matchRepository;
        this.championRepository = championRepository;
        this.gameVersionRepository = gameVersionRepository;
    }

    @Transactional
    public void processMatch(MatchDTO matchDTO, RegionCode regionCode) {
        String matchId = matchDTO.getMetadata().getMatchId();
        MatchInfoDTO info = matchDTO.getInfo();

        if (matchId == null || info == null) {
            log.error("Null matchId/info found");
            return;
        }

        try {
            // Save match
            log.info("Saving matchId={}", matchId);
            String gamePatch = extractGamePatch(info.getGameVersion()); // Different version identity
            GameVersion gameVersion = getOrCreateGameVersion(gamePatch);
            Match match = new Match(matchId, Instant.ofEpochMilli(info.getGameCreation()), gameVersion);
            matchRepository.save(match);

            // Save champions from match
            List<Champion> champions = processChampions(matchId, info.getParticipants(), info.getTeams(), gameVersion, regionCode);
            championRepository.saveAll(champions);

            log.info("Completed processing matchId={}", matchId);
        } catch (Exception e) {
            log.error("Failed to process matchId={}: {}", matchId, e.getMessage());
        }
    }

    private Set<Short> fetchChampionIds(List<ChampionDTO> championDTOS) {
        return championDTOS.stream()
                .map(ChampionDTO::getChampionId)
                .collect(Collectors.toSet());
    }

    private Set<Short> fetchBannedChampionIds(List<TeamDTO> teamDTOS) {
        return teamDTOS.stream()
                .flatMap(teamDTO -> teamDTO.getBans().stream())
                .map(BanDTO::getChampionId)
                .filter(id -> id != NO_CHAMPION_BAN_ID)
                .collect(Collectors.toSet());
    }

    // Update each champion's stats and bans from match
    private List<Champion> processChampions(String matchId,
                                            List<ChampionDTO> championDTOS,
                                            List<TeamDTO> teamDTOS,
                                            GameVersion gameVersion,
                                            RegionCode regionCode) {
        log.info("Processing matchId={} champions in {} on {}", matchId, regionCode.getValue(), gameVersion);

        Set<Short> championIds = fetchChampionIds(championDTOS);
        Set<Short> bannedChampionIds = fetchBannedChampionIds(teamDTOS);
        championIds.addAll(bannedChampionIds);

        List<Champion> championList = championRepository.findAllByIdsWithStats(championIds);
        Map<Short, Champion> championMap = championList.stream()
                .collect(Collectors.toMap(Champion::getId, c -> c));

        // Process participants
        championDTOS.forEach(championDTO -> {
            Short championId = championDTO.getChampionId();
            Champion champion = championMap.computeIfAbsent(championId, id -> new Champion(championId, championDTO.getChampionName()));
            champion.updateNameIfBlank(championDTO.getChampionName());
            champion.getChampionStats(gameVersion, regionCode)
                    .addOrUpdatePositionStats(championDTO);
        });

        // Process bans
        bannedChampionIds.forEach(id -> {
            Champion champion = championMap.computeIfAbsent(id, Champion::new);
            champion.getChampionStats(gameVersion, regionCode)
                    .incrementBans();
        });

        return new ArrayList<>(championMap.values());
    }

    private String extractGamePatch(String gameVersion) {
        String[] parts = gameVersion.split("\\.");
        return parts[0] + "." + parts[1];
    }

    private GameVersion getOrCreateGameVersion(String gamePatch) {
        return gameVersionRepository.findByPatch(gamePatch)
                .orElseGet(() -> gameVersionRepository.save(new GameVersion(gamePatch)));
    }
}
