package com.riftstats.services;

import com.riftstats.dtos.ChampionStatsDTO;
import com.riftstats.enums.ChampionTier;
import com.riftstats.enums.RegionCode;
import com.riftstats.models.*;
import com.riftstats.repositories.ChampionRepository;
import com.riftstats.repositories.GameVersionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChampionService {

    private static final double WIN_RATE_WEIGHT = 0.6;
    private static final double BAN_RATE_WEIGHT = 0.25;
    private static final double PICK_RATE_WEIGHT = 0.15;

    private final ChampionRepository championRepository;
    private final GameVersionRepository gameVersionRepository;
    private final MatchService matchService;

    @Autowired
    public ChampionService(ChampionRepository championRepository,
                           GameVersionRepository gameVersionRepository,
                           MatchService matchService) {
        this.championRepository = championRepository;
        this.gameVersionRepository = gameVersionRepository;
        this.matchService = matchService;
    }

    private ChampionTier computeChampionTier(double winRate, double banRate, double pickRate) {
        double tierScore = (WIN_RATE_WEIGHT * winRate) +
                            (BAN_RATE_WEIGHT * banRate) +
                            (PICK_RATE_WEIGHT * pickRate);
        return Arrays.stream(ChampionTier.values())
                .filter(tier -> tierScore >= tier.getScoreThreshold())
                .findFirst()
                .orElse(ChampionTier.D);
    }

    // Sort by win rate and tier in descending order
    private Comparator<ChampionStatsDTO> getWinRateComparator() {
        return Comparator.comparingDouble(ChampionStatsDTO::getWinRate)
                .reversed()
                .thenComparing(championStatsDTO -> championStatsDTO.getTier().getScoreThreshold());
    }

    public List<ChampionStatsDTO> getRegionVersionWinRates(String regionCodeString, String patch) {
        Optional<RegionCode> regionCode = RegionCode.fromValue(regionCodeString);
        Optional<GameVersion> gameVersion = gameVersionRepository.findByPatch(patch);
        if (regionCode.isEmpty() || gameVersion.isEmpty()) return Collections.emptyList();

        int totalMatchesThisPatch = matchService.getMatchesByPatch(patch).size();
        return getRegionVersionWinRates(regionCode.get(), gameVersion.get(), totalMatchesThisPatch);
    }

    public List<ChampionStatsDTO> getGlobalVersionWinRates(String patch) {
        Optional<GameVersion> gameVersion = gameVersionRepository.findByPatch(patch);
        if (gameVersion.isEmpty()) return Collections.emptyList();

        int totalMatchesThisPatch = matchService.getMatchesByPatch(patch).size();
        return getGlobalVersionWinRates(gameVersion.get(), totalMatchesThisPatch);
    }

    private List<ChampionStatsDTO> getRegionVersionWinRates(RegionCode regionCode, GameVersion gameVersion, int totalMatchesThisPatch) {
        return championRepository.findAllByRegionAndVersionWithStats(gameVersion.getId(), regionCode).stream()
                .flatMap(champion -> {
                    ChampionStatsKey key = new ChampionStatsKey(champion.getId(), gameVersion.getId(), regionCode);
                    ChampionStats championStats = champion.getChampionStatsMap().get(key);
                    return championStats.getPositionalChampionStats().stream()
                            .map(posStat -> convertToChampionStatsDTO(champion.getName(), posStat, championStats.getBans(), totalMatchesThisPatch));
                })
                .sorted(getWinRateComparator())
                .toList();
    }

    private List<ChampionStatsDTO> getGlobalVersionWinRates(GameVersion gameVersion, int totalMatchesThisPatch) {
        return championRepository.findAllByVersionWithStats(gameVersion.getId()).stream()
                .flatMap(champion -> {
                    // Aggregate bans
                    int totalBans = champion.getChampionStatsMap().values().stream()
                            .mapToInt(ChampionStats::getBans)
                            .sum();

                    // Aggregate positional stats
                    Map<String, PositionalChampionStats> aggregatedStats = champion.getChampionStatsMap().values().stream()
                            .flatMap(championStats -> championStats.getPositionalChampionStats().stream())
                            .collect(Collectors.toMap(
                                    PositionalChampionStats::getPosition,
                                    PositionalChampionStats::new,
                                    (existing, incoming) -> {
                                        existing.updateWinLoss(incoming);
                                        return existing;
                                    }
                            ));

                    // Convert to Champion Stats DTO
                    return aggregatedStats.values().stream()
                            .map(posStat -> convertToChampionStatsDTO(champion.getName(), posStat, totalBans, totalMatchesThisPatch));
                })
                .sorted(getWinRateComparator())
                .toList();
    }

    private ChampionStatsDTO convertToChampionStatsDTO(String championName,
                                                       PositionalChampionStats positionalChampionStats,
                                                       int bans,
                                                       int totalMatchesThisPatch) {
        int numMatches = positionalChampionStats.getWins() + positionalChampionStats.getLosses();
        double winRate = (double) positionalChampionStats.getWins() / numMatches;
        double banRate = (double) bans / totalMatchesThisPatch;
        double pickRate = (double) numMatches / totalMatchesThisPatch;

        return new ChampionStatsDTO(
                championName,
                positionalChampionStats.getPosition(),
                computeChampionTier(winRate, banRate, pickRate),
                winRate,
                banRate,
                pickRate,
                numMatches
        );
    }
}
