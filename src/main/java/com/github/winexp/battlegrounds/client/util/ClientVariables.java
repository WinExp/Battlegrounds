package com.github.winexp.battlegrounds.client.util;

import com.github.winexp.battlegrounds.util.data.ModGameConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientVariables {
    public static ModGameConfig gameConfig;

    static {
        resetGameConfig();
    }

    public static void resetGameConfig() {
        gameConfig = ModGameConfig.DEFAULT;
    }
}
