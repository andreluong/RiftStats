package com.riftstats.models;

import com.riftstats.enums.RegionCode;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class ChampionStatsKey implements Serializable {
    @Column(name = "champion_id", nullable = false)
    private Short championId;

    @Column(name = "version_id", nullable = false)
    private Long versionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "region_code", nullable = false)
    private RegionCode regionCode;

    public ChampionStatsKey() {}

    public ChampionStatsKey(Short championId, Long versionId, RegionCode regionCode) {
        this.championId = championId;
        this.versionId = versionId;
        this.regionCode = regionCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ChampionStatsKey that = (ChampionStatsKey) o;
        return Objects.equals(championId, that.championId) &&
                Objects.equals(versionId, that.versionId) &&
                Objects.equals(regionCode, that.regionCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(championId, versionId, regionCode);
    }
}
