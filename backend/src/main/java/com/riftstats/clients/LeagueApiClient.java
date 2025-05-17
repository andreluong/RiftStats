package com.riftstats.clients;

import com.riftstats.dtos.LeagueDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface LeagueApiClient {
    @GetMapping("/lol/league/v4/challengerleagues/by-queue/RANKED_SOLO_5x5")
    LeagueDTO getChallengerList(@RequestParam("api_key") String riotApiKey);

}