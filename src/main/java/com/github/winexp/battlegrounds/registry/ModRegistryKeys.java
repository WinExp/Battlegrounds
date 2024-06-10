package com.github.winexp.battlegrounds.registry;

import com.github.winexp.battlegrounds.game.GameTrigger;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModRegistryKeys {
    public static final RegistryKey<Registry<GameTrigger>> GAME_TRIGGER = RegistryKey.ofRegistry(new Identifier("battlegrounds", "game_trigger"));
}
