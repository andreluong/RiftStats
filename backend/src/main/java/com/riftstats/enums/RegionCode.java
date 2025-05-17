package com.riftstats.enums;

import java.util.Optional;

public enum RegionCode {
    BR1("br1", "Brazil"),
    EUN1("eun1", "Europe Nordic & East"),
    EUW1("euw1", "Europe West"),
    JP1("jp1", "Japan"),
    KR("kr", "Republic of Korea"),
    LA1("la1", "Latin America North"),
    LA2("la2", "Latin America South"),
    NA1("na1", "North America"),
    OC1("oc1", "Oceania"),
    TR1("tr1", "Turkey"),
    RU("ru", "Russia"),
    SEA("sea", "Southeast Asia"),
    TW2("tw2", "Taiwan, Hong Kong, and Macao"),
    VN2("vn2", "Vietnam"),
    SG2("sg2", "Singapore, Malaysia, and Indonesia"),
    ME1("me1", "Middle East");

    private final String value;
    private final String label;

    RegionCode(String value, String label) {
        this.value = value;
        this.label = label;
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


    @Override
    public String toString() {
        return String.format("%s (%s)", label, value);
    }
}
