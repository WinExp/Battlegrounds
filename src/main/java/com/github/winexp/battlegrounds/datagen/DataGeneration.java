package com.github.winexp.battlegrounds.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModAdvancementProvider::new);
        pack.addProvider(ModChestLootTableProvider::new);
        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(ModGamePropertiesProvider::new);
    }
}
