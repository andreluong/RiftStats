package com.riftstats.models;

import com.riftstats.dtos.ChampionDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "positional_champion_stats")
@NoArgsConstructor
public class PositionalChampionStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "champion_id", referencedColumnName = "champion_id"),
            @JoinColumn(name = "version_id", referencedColumnName = "version_id"),
            @JoinColumn(name = "region_code", referencedColumnName = "region_code")
    })
    private ChampionStats championStats;

    private String position;
    private int wins;
    private int losses;

    public PositionalChampionStats(ChampionStats championStats, ChampionDTO championDTO) {
        this.championStats = championStats;
        this.position = championDTO.getTeamPosition();
        this.wins = championDTO.isWin() ? 1 : 0;
        this.losses = !championDTO.isWin() ? 1 : 0;
    }

    public PositionalChampionStats(PositionalChampionStats copy) {
        this.championStats = copy.getChampionStats();
        this.position = copy.getPosition();
        this.wins = copy.getWins();
        this.losses = copy.losses;
    }

    public void updateWinLoss(ChampionDTO championDTO) {
        if (championDTO.isWin()) {
            wins++;
        } else {
            losses++;
        }
    }

    public void updateWinLoss(PositionalChampionStats other) {
        this.wins += other.wins;
        this.losses += other.losses;
    }

    @Override
    public String toString() {
        return String.format("%s (wins=%d, losses=%d)", position, wins, losses);
    }
}
