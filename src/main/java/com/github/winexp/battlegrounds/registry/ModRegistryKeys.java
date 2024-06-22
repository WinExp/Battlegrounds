package com.github.winexp.battlegrounds.registry;

import com.github.winexp.battlegrounds.discussion.vote.VoteMode;
import com.github.winexp.battlegrounds.discussion.vote.VotePreset;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModRegistryKeys {
    public static final RegistryKey<Registry<VoteMode>> VOTE_MODE = of("vote_mode");
    public static final RegistryKey<Registry<VotePreset>> VOTE_PRESET = of("vote_preset");

    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(new Identifier("battlegrounds", id));
    }
}
