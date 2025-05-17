package com.riftstats.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// NOTE: May also be known as participant from Riot API
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChampionDTO {
    private Short championId;
    private String championName;
    private String teamPosition;
    private int participantId;
    private boolean win;
}
