package com.github.winexp.battlegrounds.util;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class Constants {
    public static final String MOD_ID = "Battlegrounds";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
}

