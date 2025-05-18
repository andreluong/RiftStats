package com.riftstats.enums;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static com.riftstats.enums.LeagueTier.CHALLENGER;
import static com.riftstats.enums.LeagueTier.GRANDMASTER;

public enum RegionCode {
    BR1("br1", "Brazil", new EnumMap<>(Map.of(CHALLENGER, 200, GRANDMASTER, 500))),
    EUN1("eun1", "Europe Nordic & East", new EnumMap<>(Map.of(CHALLENGER, 200, GRANDMASTER, 500))),
    EUW1("euw1", "Europe West", new EnumMap<>(Map.of(CHALLENGER, 300, GRANDMASTER, 700))),
    JP1("jp1", "Japan", new EnumMap<>(Map.of(CHALLENGER, 50, GRANDMASTER, 100))),
    KR("kr", "Republic of Korea", new EnumMap<>(Map.of(CHALLENGER, 300, GRANDMASTER, 700))),
    LA1("la1", "Latin America North", new EnumMap<>(Map.of(CHALLENGER, 200, GRANDMASTER, 500))),
    LA2("la2", "Latin America South", new EnumMap<>(Map.of(CHALLENGER, 200, GRANDMASTER, 500))),
    NA1("na1", "North America", new EnumMap<>(Map.of(CHALLENGER, 300, GRANDMASTER, 700))),
    OC1("oc1", "Oceania", new EnumMap<>(Map.of(CHALLENGER, 50, GRANDMASTER, 100))),
    TR1("tr1", "Turkey", new EnumMap<>(Map.of(CHALLENGER, 200, GRANDMASTER, 500))),
    RU("ru", "Russia", new EnumMap<>(Map.of(CHALLENGER, 50, GRANDMASTER, 100))),
    SEA("sea", "Southeast Asia", new EnumMap<>(Map.of(CHALLENGER, 200, GRANDMASTER, 500))),
    TW2("tw2", "Taiwan, Hong Kong, and Macao", new EnumMap<>(Map.of(CHALLENGER, 200, GRANDMASTER, 500))),
    VN2("vn2", "Vietnam", new EnumMap<>(Map.of(CHALLENGER, 300, GRANDMASTER, 700))),
    SG2("sg2", "Singapore, Malaysia, and Indonesia", new EnumMap<>(Map.of(CHALLENGER, 50, GRANDMASTER, 100))),
    ME1("me1", "Middle East", new EnumMap<>(Map.of(CHALLENGER, 50, GRANDMASTER, 100)));

    private final String value;
    private final String label;
    private final EnumMap<LeagueTier, Integer> leagueSizes;

    RegionCode(String value, String label, EnumMap<LeagueTier, Integer> leagueSizes) {
        this.value = value;
        this.label = label;
        this.leagueSizes = leagueSizes;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static Optional<RegionCode> fromValue(String value) {
        for (RegionCode regionCode : RegionCode.values()) {
            if (regionCode.getValue().equals(value)) {
                return Optional.of(regionCode);
            }
        }
        return Optional.empty();
    }

    public Integer getLeagueSize(LeagueTier tier) {
        return leagueSizes.getOrDefault(tier, 25);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", label, value);
    }
}
