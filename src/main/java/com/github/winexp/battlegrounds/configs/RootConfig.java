package com.github.winexp.battlegrounds.configs;

public class RootConfig {
    public long voteTimeoutTicks = 600;
    public long voteCooldownTicks = 600;
    public long randomTpCooldownTicks = 600;
    public int serverCloseDelaySeconds = 10;
    public int gameStartDelaySeconds = 10;
    public int pvpModeBeginBorderNum = 2;
    public BorderConfig border = new BorderConfig();

    public static class BorderConfig{
        public int initialSize = 5000;
        public long resizeTimeTicks = 6000;
        public int resizeDelayTicks = 2400;
        public int resizeBlocks = 500;
        public int resizeNum = 3;
        public int finalSize = 500;
    }
}
