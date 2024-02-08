package com.github.winexp.battlegrounds.configs;

import java.util.Map;

public class GameProgress {
    public PVPMode pvpMode = PVPMode.PEACEFUL;
    public GameStage gameStage = GameStage.IDLE;
    public int currentLap = 0;
    public long resizeLapTimer = 0;
    public boolean hasEffects = false;
    public Map<String, String> players = Map.of();

    public enum GameStage {
        IDLE, RESET_WORLD, WAIT_PLAYER, DEVELOP, DEATHMATCH;

        public boolean isDeathmatch() {
            return this == DEATHMATCH;
        }

        public boolean isResetWorld() {
            return this == RESET_WORLD;
        }

        public boolean isIdle() {
            return this == IDLE;
        }

        public boolean isPreparing() {
            return this.ordinal() <= WAIT_PLAYER.ordinal() && this != IDLE;
        }

        public boolean isStarted() {
            return this.ordinal() >= DEVELOP.ordinal();
        }
    }

    public enum PVPMode {
        PEACEFUL, NO_PVP, PVP_MODE
    }
}
