package com.riftstats.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchMetadataDTO {
    private String dataVersion;
    private String matchId;
    private List<String> participants;
}
