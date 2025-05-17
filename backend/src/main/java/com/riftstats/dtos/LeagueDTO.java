package com.riftstats.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeagueDTO {
    private String tier;        // Challenger, Grandmaster, Bronze, etc
    private String leagueId;    // Unique id
    private String queue;       // Solo 5x5, Flex SR, Flex TT
    private List<PlayerDTO> entries;
}
