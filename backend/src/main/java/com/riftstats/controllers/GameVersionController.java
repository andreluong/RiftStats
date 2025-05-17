package com.riftstats.controllers;

import com.riftstats.models.GameVersion;
import com.riftstats.services.GameVersionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/game-versions")
public class GameVersionController {

    private final GameVersionService gameVersionService;

    @Autowired
    public GameVersionController(GameVersionService gameVersionService) {
        this.gameVersionService = gameVersionService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<GameVersion>> getAllVersions(HttpServletRequest request) {
        log.info("Received request for all game patches from IP={}", request.getRemoteAddr());
        List<GameVersion> gameVersions = gameVersionService.getAllVersions();
        return ResponseEntity.ok(gameVersions);
    }

    @GetMapping("/latest")
    public ResponseEntity<GameVersion> getLatestVersion(HttpServletRequest request) {
        log.info("Received request for latest game patch from IP={}", request.getRemoteAddr());
        return gameVersionService.getLatestVersion()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
