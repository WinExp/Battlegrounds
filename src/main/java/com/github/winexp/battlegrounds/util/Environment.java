package com.github.winexp.battlegrounds.util;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class Environment {
    public final static String MOD_ID = "Battlegrounds";
    public final static Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    public final static Path GAME_PATH = FabricLoader.getInstance().getGameDir();
}

