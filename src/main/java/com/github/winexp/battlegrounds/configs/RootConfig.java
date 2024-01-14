package com.github.winexp.battlegrounds.configs;

public class RootConfig {
    public long voteTimeoutTicks = 600;
    public long voteCooldownTicks = 600;
    public long randomTpCooldownTicks = 600;
    public int serverCloseDelaySeconds = 10;
    public int gameStartDelaySeconds = 10;
    public BorderConfig border = new BorderConfig();

    public static class BorderConfig{
        public int initialBorderSize = 1000;
    }
}
