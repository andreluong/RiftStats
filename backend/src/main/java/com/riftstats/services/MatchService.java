package com.riftstats.services;

import com.riftstats.models.Match;
import com.riftstats.repositories.MatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MatchService {

    private final MatchRepository matchRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> getMatchesByPatch(String patch) {
        return matchRepository.findAllByGameVersion_Patch(patch);
    }

    public int getNumberOfMatches() {
        return matchRepository.findAll().size();
    }

}
