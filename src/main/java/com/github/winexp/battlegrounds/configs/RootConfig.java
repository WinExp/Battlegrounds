package com.github.winexp.battlegrounds.configs;

@SuppressWarnings("CanBeFinal")
public class RootConfig {
    public long discussionTimeoutTicks = 600;
    public long discussionCooldownTicks = 600;
    public long randomTpCooldownTicks = 600;
    public int serverCloseDelaySeconds = 10;
    public int gameStartDelaySeconds = 10;
    public AttributeConfig attribute = new AttributeConfig();
    public BorderConfig border = new BorderConfig();

    public static class AttributeConfig {
        public int genericMaxHealth = 40;
        public int deathmatchMaxHealth = 20;
    }

    public static class BorderConfig {
        public int initialSize = 5000;
        public long resizeTimeTicks = 4 * 60 * 20;
        public int resizeDelayTicks = 2 * 60 * 20;
        public int resizeBlocks = 500;
        public int resizeNum = 4;
        public int pvpModeBeginBorderNum = 2;
        public int finalBorderNum = 3;
        public int deathmatchBeginBorderNum = 3;
        public DeathmatchBorderConfig deathmatch = new DeathmatchBorderConfig();

        public static class DeathmatchBorderConfig {
            public int initialSize = 300;
            public long resizeDelayTicks = 5 * 60 * 20;
            public int finalSize = 100;
        }
    }
}
