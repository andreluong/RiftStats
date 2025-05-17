package com.riftstats.models;

import com.riftstats.dtos.ChampionDTO;
import com.riftstats.enums.RegionCode;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "champion_stats")
public class ChampionStats {

    @EmbeddedId
    private ChampionStatsKey key;

    @ManyToOne(optional = false)
    @MapsId("championId")
    @JoinColumn(name = "champion_id", nullable = false)
    private Champion champion;

    @ManyToOne(optional = false)
    @MapsId("versionId")
    @JoinColumn(name = "version_id", nullable = false)
    private GameVersion gameVersion;

    private int bans;

    @OneToMany(mappedBy = "championStats", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PositionalChampionStats> positionalChampionStats = new ArrayList<>();

    public ChampionStats() {
        this.key = new ChampionStatsKey();
        this.champion = new Champion();
        this.gameVersion = new GameVersion();
        this.bans = 0;
    }

    public ChampionStats(Champion champion, GameVersion gameVersion, RegionCode regionCode) {
        this.key = new ChampionStatsKey(champion.getId(), gameVersion.getId(), regionCode);
        this.champion = champion;
        this.gameVersion = gameVersion;
        this.bans = 0;
    }

    public void incrementBans() {
        bans++;
    }

    public void addOrUpdatePositionStats(ChampionDTO championDTO) {
        positionalChampionStats.stream()
                .filter(stat -> stat.getPosition().equals(championDTO.getTeamPosition()))
                .findFirst()
                .ifPresentOrElse(
                        pos -> pos.updateWinLoss(championDTO),
                        () -> positionalChampionStats.add(new PositionalChampionStats(this, championDTO))
                );
    }
}
