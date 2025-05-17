package com.riftstats.dtos;

import com.riftstats.enums.ChampionTier;
import lombok.AllArgsConstructor;
import lombok.Data;

// DTO to send to client

@Data
@AllArgsConstructor
public class ChampionStatsDTO {
    private String name;
    private String position;
    private ChampionTier tier;
    private double winRate;
    private double banRate;
    private double pickRate;
    private int matches;
}
