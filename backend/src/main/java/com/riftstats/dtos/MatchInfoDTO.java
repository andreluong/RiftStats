package com.riftstats.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchInfoDTO {
    private long gameId;
    private long gameCreation;
    private String gameVersion;
    private List<ChampionDTO> participants;
    private List<TeamDTO> teams;
}
