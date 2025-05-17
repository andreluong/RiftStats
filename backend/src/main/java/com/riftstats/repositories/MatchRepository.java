package com.riftstats.repositories;

import com.riftstats.models.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByGameVersion_Patch(String patch);

    // Find matches by a list of matchIds
    List<Match> findByMatchIdIn(Collection<String> matchIds);
}
