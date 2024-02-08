package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.configs.RootConfig;
import net.minecraft.server.MinecraftServer;

public class Variable {
    public final static Variable INSTANCE = new Variable();

    public MinecraftServer server;
    public RootConfig config;
    public GameProgress progress;

    private Variable() {
    }
}
