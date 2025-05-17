package com.riftstats.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "matchId", nullable = false)
    private String matchId; // RegionCode_GameId

    @Column(name = "creation_time", nullable = false)
    private Instant creationTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "version_id", nullable = false)
    private GameVersion gameVersion;

    public Match(String matchId, Instant creationTime, GameVersion gameVersion) {
        this.matchId = matchId;
        this.creationTime = creationTime;
        this.gameVersion = gameVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Match that = (Match) o;
        return Objects.equals(matchId, that.matchId) &&
                Objects.equals(creationTime, that.creationTime) &&
                Objects.equals(gameVersion, that.gameVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, creationTime, gameVersion);
    }
}
