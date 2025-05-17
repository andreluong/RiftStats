package com.riftstats.clients;

import feign.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface MatchApiClient {

    @GetMapping("/lol/match/v5/matches/by-puuid/{puuid}/ids?type=ranked&start=0&count=20")
    Response getMatchList(@PathVariable String puuid, @RequestParam("api_key") String riotApiKey);

    @GetMapping("/lol/match/v5/matches/{matchId}")
    Response getMatch(@PathVariable String matchId, @RequestParam("api_key") String riotApiKey);

}
