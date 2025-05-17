package com.riftstats.dtos;

import lombok.Data;

@Data
public class MatchDTO {
    private MatchMetadataDTO metadata;
    private MatchInfoDTO info;
}
