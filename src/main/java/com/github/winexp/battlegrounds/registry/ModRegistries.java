package com.github.winexp.battlegrounds.registry;

import com.github.winexp.battlegrounds.game.GameTrigger;
import com.github.winexp.battlegrounds.game.GameTriggers;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class ModRegistries {
    private static final HashMap<Identifier, Runnable> INITIALIZERS = new HashMap<>();
    public static final DefaultedRegistry<GameTrigger> GAME_TRIGGER = registerDefaulted(ModRegistryKeys.GAME_TRIGGER, "none", registry -> GameTriggers.initialize());

    private static <T> DefaultedRegistry<T> registerDefaulted(RegistryKey<Registry<T>> registryKey, String defaultId, Initializer<T> initializer) {
        DefaultedRegistry<T> registry = FabricRegistryBuilder.createDefaulted(registryKey, new Identifier("battlegrounds", defaultId)).buildAndRegister();
        INITIALIZERS.put(registryKey.getValue(), () -> initializer.initialize(registry));
        return registry;
    }

    public static void bootstrap() {
        INITIALIZERS.forEach((registryKey, initializer) -> initializer.run());
    }

    @FunctionalInterface
    public interface Initializer<T> {
        void initialize(Registry<T> registry);
    }
}
