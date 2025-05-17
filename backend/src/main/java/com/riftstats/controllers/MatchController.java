package com.riftstats.controllers;

import com.riftstats.models.Match;
import com.riftstats.services.MatchService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Match>> getMatchesByPatch(@RequestParam("patch") String patch,
                                                         HttpServletRequest request) {
        log.info("Received request for all matches with patch={} from IP={}", patch, request.getRemoteAddr());
        List<Match> matches = matchService.getMatchesByPatch(patch);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getNumberOfMatches(HttpServletRequest request) {
        log.info("Received request for number of matches from IP={}", request.getRemoteAddr());
        int count = matchService.getNumberOfMatches();
        return ResponseEntity.ok(count);
    }
}
