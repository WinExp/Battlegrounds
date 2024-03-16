package com.github.winexp.battlegrounds.config;

public class RootConfig {
    public final CooldownConfig cooldown = new CooldownConfig();
    public final DelayConfig delay = new DelayConfig();
    public final BorderConfig border = new BorderConfig();

    public static class CooldownConfig {
        public final long startGameVoteCooldownTicks = 30 * 20;
        public final long randomTpCooldownTicks = 2 * 60 * 20;
        public final long randomTpDamagedCooldownTicks = 20 * 20;
    }

    public static class DelayConfig {
        public final int serverCloseDelaySeconds = 10;
        public final int gameStartDelaySeconds = 10;
    }

    public static class BorderConfig {
        public final int initialSize = 5000;
        public final int resizeBlocks = 500;
        public final int totalAmount = 4;
        public final TimeConfig time = new TimeConfig();
        public final BorderOrdinalConfig borderOrdinal = new BorderOrdinalConfig();
        public final DeathmatchConfig deathmatch = new DeathmatchConfig();

        public static class TimeConfig {
            public final long resizeSpendTicks = 4 * 60 * 20;
            public final int resizeDelayTicks = 3 * 60 * 20;
        }

        public static class BorderOrdinalConfig {
            public final int pvpEnabledBorderOrdinal = 2;
            public final int finalBorderOrdinal = 3;
            public final int deathmatchBeginBorderOrdinal = 3;
        }

        public static class DeathmatchConfig {
            public final int initialSize = 300;
            public final long resizeSpendTicks = 5 * 60 * 20;
            public final int finalSize = 150;
        }
    }
}
