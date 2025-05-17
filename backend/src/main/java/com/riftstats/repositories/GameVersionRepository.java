package com.riftstats.repositories;

import com.riftstats.models.GameVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameVersionRepository extends JpaRepository<GameVersion, Long> {
    Optional<GameVersion> findByPatch(String patch);

    @Query(
            value = "SELECT * FROM game_versions " +
                    "ORDER BY CAST(SPLIT_PART(patch, '.', 1) AS INTEGER) DESC, " +
                    "CAST(SPLIT_PART(patch, '.', 2) AS INT) DESC",
            nativeQuery = true
    )
    List<GameVersion> findAllOrderedByPatchDesc();

    @Query(
            value = "SELECT * FROM game_versions " +
                    "ORDER BY CAST(SPLIT_PART(patch, '.', 1) AS INTEGER) DESC, " +
                    "CAST(SPLIT_PART(patch, '.', 2) AS INTEGER) DESC " +
                    "LIMIT 1",
            nativeQuery = true
    )
    Optional<GameVersion> findLatestVersion();
}
