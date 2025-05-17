package com.riftstats.enums;

import java.util.List;
import java.util.Optional;

import static com.riftstats.enums.RegionCode.*;

public enum RegionGroup {
    AMERICAS("americas", NA1, BR1, LA1, LA2),
    ASIA("asia", KR, JP1),
    EUROPE("europe", EUN1, EUW1, ME1, TR1, RU),
    SEA("sea", OC1, SG2, TW2, VN2);

    private final String value;
    private final List<RegionCode> regionCodes;

    RegionGroup(String value, RegionCode... regionCodes) {
        this.value = value;
        this.regionCodes = List.of(regionCodes);
    }

    public String getValue() {
        return value;
    }

    public List<RegionCode> getRegionCodes() {
        return regionCodes;
    }

    public static Optional<RegionGroup> fromValue(String value) {
        for (RegionGroup regionGroup : RegionGroup.values()) {
            if (regionGroup.getValue().equals(value)) {
                return Optional.of(regionGroup);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", value, regionCodes.toString());
    }
}