package com.github.winexp.battlegrounds.util;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class Environment {
    public final static String MOD_ID = "Battlegrounds";
    public final static Logger LOGGER = LogManager.getLogger(MOD_ID);
    public final static Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
}

