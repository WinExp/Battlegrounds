package com.github.winexp.battlegrounds.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModDynamicRegistryGenerator::new);
        pack.addProvider(ModAdvancementGenerator::new);
        pack.addProvider(ModChestLootTableGenerator::new);
        pack.addProvider(ModItemTagGenerator::new);
        pack.addProvider(ModFluidTagGenerator::new);
        pack.addProvider(ModItemModelGenerator::new);
        pack.addProvider(ModRecipeGenerator::new);
        pack.addProvider(ModGamePropertiesGenerator::new);
    }
}
