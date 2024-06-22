package com.github.winexp.battlegrounds;

import com.github.winexp.battlegrounds.client.config.ClientRootConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Consumer;

public class ModMenuApiImpl implements ModMenuApi {
    public static Screen createConfigScreen(Screen parent) {
        return ClientRootConfig.HANDLER.generateGui().generateScreen(parent);
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuApiImpl::createConfigScreen;
    }

    @Override
    public void attachModpackBadges(Consumer<String> consumer) {
        consumer.accept("battlegrounds");
    }
}
