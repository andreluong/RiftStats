package com.riftstats.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDTO {
    private String puuid;       // Player's encrypted puuid.
    private String tier;
    private String rank;        // Division within tier
}
