package com.github.winexp.battlegrounds.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class Constants {
    public static final String MOD_ID = "battlegrounds";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    public static final Path DELETE_WORLD_TMP_FILE_PATH = FabricLoader.getInstance().getGameDir().resolve(Path.of("reset_world.session"));
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
}

