package com.riftstats.repositories;

import com.riftstats.enums.RegionCode;
import com.riftstats.models.Champion;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChampionRepository extends JpaRepository<Champion, Short> {
    @Query("""
        SELECT DISTINCT c
        FROM Champion c
        LEFT JOIN FETCH c.championStatsMap championStats
        LEFT JOIN FETCH championStats.positionalChampionStats
        WHERE c.id in :ids
    """)
    List<Champion> findAllByIdsWithStats(Collection<Short> ids);

    @Query("""
           SELECT DISTINCT c
           FROM Champion c
           LEFT JOIN FETCH c.championStatsMap championStats
           LEFT JOIN FETCH championStats.positionalChampionStats
           WHERE championStats.id.versionId = :versionId
    """)
    List<Champion> findAllByVersionWithStats(Long versionId);

    @Query("""
           SELECT DISTINCT c
           FROM Champion c
           LEFT JOIN FETCH c.championStatsMap championStats
           LEFT JOIN FETCH championStats.positionalChampionStats
           WHERE championStats.id.versionId = :versionId AND championStats.id.regionCode = :regionCode
    """)
    List<Champion> findAllByRegionAndVersionWithStats(Long versionId, RegionCode regionCode);

}
