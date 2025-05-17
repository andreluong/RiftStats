package com.riftstats.models;

import com.riftstats.enums.RegionCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@Table(name = "champions")
@NoArgsConstructor
public class Champion {

    @Id
    private short id;

    private String name;

    @OneToMany(mappedBy = "champion", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "key")
    private Map<ChampionStatsKey, ChampionStats> championStatsMap = new HashMap<>();

    public Champion(short id) {
        this.id = id;
        this.name = "";
    }

    public Champion(short id, String name) {
        this(id);
        this.name = name;
    }

    public void updateNameIfBlank(String name) {
        if (this.name.isBlank()) {
            this.name = name;
        }
    }

    public ChampionStats getChampionStats(GameVersion gameVersion, RegionCode regionCode) {
        ChampionStatsKey key = new ChampionStatsKey(id, gameVersion.getId(), regionCode);
        return championStatsMap.computeIfAbsent(key, k -> new ChampionStats(this, gameVersion, regionCode));
    }

    @Override
    public String toString() {
        // Build regional data for champion string
        StringBuilder regionString = new StringBuilder();
        for (var stat : championStatsMap.entrySet()) {
            ChampionStatsKey key = stat.getKey();
            ChampionStats cs = stat.getValue();

            // Build position data string
            StringBuilder positionsString = new StringBuilder();
            for (int i = 0; i < cs.getPositionalChampionStats().size(); i++) {
                positionsString.append(cs.getPositionalChampionStats().get(i).toString());
                if (i < cs.getPositionalChampionStats().size()-1) {
                    positionsString.append(", ");
                }
            }
            regionString.append(String.format("%s (bans=%d, positions=[%s])%n", key.getRegionCode().getLabel(), cs.getBans(), positionsString));
        }

        return String.format("%s (%s): %n%s", name, id, regionString);
    }
}
