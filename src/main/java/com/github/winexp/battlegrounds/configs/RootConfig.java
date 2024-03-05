package com.github.winexp.battlegrounds.configs;

@SuppressWarnings("CanBeFinal")
public class RootConfig {
    public CooldownConfig cooldown = new CooldownConfig();
    public TimeoutConfig timeout = new TimeoutConfig();
    public DelayConfig delay = new DelayConfig();
    public AttributeConfig attributes = new AttributeConfig();
    public BorderConfig border = new BorderConfig();

    public static class CooldownConfig {
        public long startGameVoteCooldownTicks = 30 * 20;
        public long randomTpCooldownTicks = 2 * 60 * 20;
        public long randomTpDamagedCooldownTicks = 20 * 20;
    }

    public static class TimeoutConfig {
        public long discussionTimeoutTicks = 30 * 20;
    }

    public static class DelayConfig {
        public int serverCloseDelaySeconds = 10;
        public int gameStartDelaySeconds = 10;
    }

    public static class AttributeConfig {
        public int genericAdditionHealth = 20;
    }

    public static class BorderConfig {
        public int initialSize = 5000;
        public int resizeBlocks = 500;
        public int totalNum = 4;
        public TimeConfig time = new TimeConfig();
        public BorderOrdinalConfig borderOrdinal = new BorderOrdinalConfig();
        public DeathmatchConfig deathmatch = new DeathmatchConfig();

        public static class TimeConfig {
            public long resizeSpendTicks = 4 * 60 * 20;
            public int resizeDelayTicks = 2 * 60 * 20;
        }

        public static class BorderOrdinalConfig {
            public int pvpEnabledBorderOrdinal = 2;
            public int finalBorderOrdinal = 3;
            public int deathmatchBeginBorderOrdinal = 3;
        }

        public static class DeathmatchConfig {
            public int initialSize = 300;
            public long resizeDelayTicks = 5 * 60 * 20;
            public int finalSize = 150;
        }
    }
}
