package com.github.winexp.battlegrounds.configs;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class GameProgress {
    public PVPMode pvpMode = PVPMode.PEACEFUL;
    public GameStage gameStage = GameStage.IDLE;
    public int currentLap = 0;
    public long resizeLapTimer = 0;
    public HashMap<UUID, PlayerPermission> players = new HashMap<>();

    public static class PlayerPermission {
        @NotNull
        @JsonAdapter(GameModeTypeAdapter.class)
        public GameMode gameMode = GameMode.SPECTATOR;
        public boolean inGame = false;
        public boolean naturalRegen = true;
        public boolean hasEffects = false;

        public static class GameModeTypeAdapter extends TypeAdapter<GameMode> {
            @Override
            public void write(JsonWriter out, GameMode value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                out.value(value.getName());
            }

            @Override
            public GameMode read(JsonReader in) throws IOException {
                String name = null;
                if (in.hasNext()) {
                    name = in.nextString();
                }
                return GameMode.byName(name, GameMode.SPECTATOR);
            }
        }
    }

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
