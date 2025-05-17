package com.riftstats.enums;

public enum ChampionTier {
    S("S", 0.38),
    A("A", 0.34),
    B("B", 0.29),
    C("C", 0.24),
    D("D", 0);

    private final String value;
    private final double scoreThreshold;

    ChampionTier(String value, double scoreThreshold) {
        this.value = value;
        this.scoreThreshold = scoreThreshold;
    }

    public String getValue() {
        return value;
    }

    public double getScoreThreshold() {
        return scoreThreshold;
    }
}
