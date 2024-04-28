package com.github.winexp.battlegrounds.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.*;

import java.util.concurrent.CompletableFuture;

public class ModDynamicRegistryGenerator extends FabricDynamicRegistryProvider {
    public ModDynamicRegistryGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        for (RegistryLoader.Entry<?> registryEntry : DynamicRegistries.getDynamicRegistries()) {
            RegistryWrapper.Impl<?> wrapper = registries.getWrapperOrThrow(registryEntry.key());
            entries.addAll(wrapper);
        }
    }

    @Override
    public String getName() {
        return "Dynamic registries for battlegrounds";
    }
}
