package com.riftstats.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDTO {
    private int teamId;
    private List<BanDTO> bans;
    private boolean win;
}
