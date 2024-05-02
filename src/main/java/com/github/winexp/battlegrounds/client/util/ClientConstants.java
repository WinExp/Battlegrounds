package com.github.winexp.battlegrounds.client.util;


import com.github.winexp.battlegrounds.client.render.FlashRenderer;
import com.github.winexp.battlegrounds.client.render.RupertsTearBlockOutlineRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientConstants {
    public static final FlashRenderer FLASH_RENDERER = new FlashRenderer();
    public static final RupertsTearBlockOutlineRenderer RUPERTS_TEAR_BLOCK_OUTLINE_RENDERER = new RupertsTearBlockOutlineRenderer();
}
