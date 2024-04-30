package com.github.winexp.battlegrounds.datagen;

import com.github.winexp.battlegrounds.world.gen.structure.StructurePools;
import com.github.winexp.battlegrounds.world.gen.structure.StructureSets;
import com.github.winexp.battlegrounds.world.gen.structure.Structures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

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

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.TEMPLATE_POOL, StructurePools::register)
                .addRegistry(RegistryKeys.STRUCTURE, Structures::register)
                .addRegistry(RegistryKeys.STRUCTURE_SET, StructureSets::register);
    }
}
