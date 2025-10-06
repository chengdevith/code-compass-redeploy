package kh.edu.istad.codecompass.enums;

import lombok.Getter;

public enum Level {
    CODE_NEWBORN(0, 10, "Code Newborn"),
    ALGORITHM_KNIGHT(10, 20, "Algorithm Knight"),
    COMPLEXITY_CONQUEROR(30, 40, "Complexity Conqueror"),
    CODE_OVERLORD(50, 60, "Code Overlord"),
    ETERNAL_CODER(60, Integer.MAX_VALUE, "Eternal Coder");

    private final int minStars;
    private final int maxStars;
    @Getter
    private final String displayName;

    Level(int minStars, int maxStars, String displayName) {
        this.minStars = minStars;
        this.maxStars = maxStars;
        this.displayName = displayName;
    }

    public static Level fromStars(int stars) {
        for (Level level : values()) {
            if (stars >= level.minStars && stars < level.maxStars) {
                return level;
            }
        }
        return CODE_NEWBORN;
    }
}

