package com.github.winexp.battlegrounds.configs;

import java.util.List;

public class GameProgress {
    public PVPMode pvpMode = PVPMode.PEACEFUL;
    public Progress progress = Progress.IDLE;
    public int currentLap = 0;
    public long resizeLapTimer = 0;
    public List<String> players = List.of();

    public enum Progress{
        IDLE, RESET_WORLD, WAIT_PLAYER, DEVELOP, PVP, DEATHMATCH;

        public boolean isResetWorld(){
            return this == WAIT_PLAYER;
        }
        public boolean isPreparing(){
            return this.ordinal() <= WAIT_PLAYER.ordinal() && this != IDLE;
        }
        public boolean isStarted(){
            return this.ordinal() >= DEVELOP.ordinal();
        }
    }

    public enum PVPMode{
        PEACEFUL, NO_PVP, PVP_MODE
    }
}
