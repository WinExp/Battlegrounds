package com.github.winexp.battlegrounds.datagen;

import com.github.winexp.battlegrounds.datagen.provider.GamePropertiesProvider;
import com.github.winexp.battlegrounds.game.GameProperties;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.function.Consumer;

public class ModGamePropertiesProvider extends GamePropertiesProvider {
    protected ModGamePropertiesProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateGameProperties(Consumer<GameProperties> exporter) {
        for (GameProperties gameProperty : GameProperties.DEFAULT_PROFILES) {
            exporter.accept(gameProperty);
        }
    }
}
