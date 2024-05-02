package com.github.winexp.battlegrounds.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class RegistryKeys extends net.minecraft.registry.RegistryKeys {
    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(new Identifier("battlegrounds", id));
    }
}
