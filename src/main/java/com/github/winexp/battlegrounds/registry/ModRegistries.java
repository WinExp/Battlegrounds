package com.github.winexp.battlegrounds.registry;

import com.github.winexp.battlegrounds.discussion.vote.VoteMode;
import com.github.winexp.battlegrounds.discussion.vote.VoteModes;
import com.github.winexp.battlegrounds.discussion.vote.VotePreset;
import com.github.winexp.battlegrounds.discussion.vote.VotePresets;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class ModRegistries {
    private static final HashMap<Identifier, Runnable> INITIALIZERS = new HashMap<>();
    public static final Registry<VoteMode> VOTE_MODE = register(ModRegistryKeys.VOTE_MODE, registry -> VoteModes.bootstrap());
    public static final Registry<VotePreset> VOTE_PRESET = register(ModRegistryKeys.VOTE_PRESET, registry -> VotePresets.bootstrap());

    private static <T> Registry<T> register(RegistryKey<Registry<T>> registryKey, Initializer<T> initializer) {
        Registry<T> registry = FabricRegistryBuilder.createSimple(registryKey).buildAndRegister();
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