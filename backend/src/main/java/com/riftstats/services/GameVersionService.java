package com.riftstats.services;

import com.riftstats.models.GameVersion;
import com.riftstats.repositories.GameVersionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GameVersionService {

    private final GameVersionRepository gameVersionRepository;

    @Autowired
    public GameVersionService(GameVersionRepository gameVersionRepository) {
        this.gameVersionRepository = gameVersionRepository;
    }

    public List<GameVersion> getAllVersions() {
        return gameVersionRepository.findAllOrderedByPatchDesc();
    }

    public Optional<GameVersion> getLatestVersion() {
        return gameVersionRepository.findLatestVersion();
    }

}
