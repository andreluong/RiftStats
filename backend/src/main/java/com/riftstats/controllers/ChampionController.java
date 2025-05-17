package com.riftstats.controllers;

import com.riftstats.dtos.ChampionStatsDTO;
import com.riftstats.services.ChampionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/champion-stats")
public class ChampionController {

    private final ChampionService championService;

    @Autowired
    public ChampionController(ChampionService championService) {
        this.championService = championService;
    }

    @GetMapping("/all/{patch}/winrates")
    public ResponseEntity<List<ChampionStatsDTO>> getGlobalVersionWinrates(@PathVariable String patch,
                                                                           HttpServletRequest request) {
        log.info("Received request for regionCode=GLOBAL patch={} champion winrates list from IP={}", patch, request.getRemoteAddr());
        List<ChampionStatsDTO> championStatsDTOS = championService.getGlobalVersionWinRates(patch);
        return ResponseEntity.ok(championStatsDTOS);
    }

    @GetMapping("/{regionCode}/{patch}/winrates")
    public ResponseEntity<List<ChampionStatsDTO>> getRegionVersionWinrates(@PathVariable String regionCode,
                                                                           @PathVariable String patch,
                                                                           HttpServletRequest request) {
        log.info("Received request for regionCode={} patch={} champion winrates list from IP={}",
                regionCode, patch, request.getRemoteAddr());
        List<ChampionStatsDTO> championStatsDTOS = championService.getRegionVersionWinRates(regionCode, patch);
        return ResponseEntity.ok(championStatsDTOS);
    }

}
