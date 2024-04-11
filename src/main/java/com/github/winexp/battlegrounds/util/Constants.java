package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.game.GameProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;

public class Constants {
    public static final String MOD_ID = "battlegrounds";
    public static final String MOD_NAME = "Battlegrounds";
    public static final ModMetadata MOD_METADATA = FabricLoader.getInstance().getModContainer(MOD_ID)
            .orElseThrow().getMetadata();
    public static final ModVersion MOD_VERSION = new ModVersion(MOD_METADATA.getVersion(), 8);
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    public static final HashMap<Identifier, GameProperties> GAME_PROPERTIES = new HashMap<>();
    public static final Path DELETE_WORLD_TMP_FILE_PATH = FabricLoader.getInstance().getGameDir()
            .resolve(Path.of("delete_world.session"));
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
}

