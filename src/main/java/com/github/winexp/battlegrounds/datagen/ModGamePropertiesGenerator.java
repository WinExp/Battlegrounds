package com.github.winexp.battlegrounds.datagen;

import com.github.winexp.battlegrounds.datagen.provider.GamePropertiesProvider;
import com.github.winexp.battlegrounds.game.GameProperties;
import com.github.winexp.battlegrounds.game.GameTriggers;
import com.github.winexp.battlegrounds.util.data.ModGameConfig;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModGamePropertiesGenerator extends GamePropertiesProvider {
    protected ModGamePropertiesGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> wrapperLookup) {
        super(output, wrapperLookup);
    }

    @Override
    public void generateGameProperties(RegistryWrapper.WrapperLookup wrapperLookup, Consumer<GameProperties> exporter) {
        exporter.accept(new GameProperties(
                new Identifier("battlegrounds", "normal"),
                ImmutableList.of(
                        new GameProperties.StageInfo(
                                List.of(GameTriggers.DEVELOP_BEGIN),
                                5000, 500, 2,
                                new GameProperties.StageInfo.ResizeTimeInfo(
                                        Duration.withMinutes(4),
                                        Duration.withMinutes(3)
                                ),
                                new ModGameConfig(true),
                                true
                        ),
                        new GameProperties.StageInfo(
                                List.of(GameTriggers.ENABLE_PVP),
                                4000, 500, 2,
                                new GameProperties.StageInfo.ResizeTimeInfo(
                                        Duration.withMinutes(3),
                                        Duration.withMinutes(2)
                                ),
                                new ModGameConfig(true),
                                true
                        ),
                        new GameProperties.StageInfo(
                                List.of(GameTriggers.DEATHMATCH_BEGIN),
                                1000, 200, 2,
                                new GameProperties.StageInfo.ResizeTimeInfo(
                                        Duration.withMinutes(3),
                                        Duration.withMinutes(2)
                                ),
                                new ModGameConfig(false),
                                false
                        )
                ),
                Duration.withMinutes(15)
        ));
    }
}
