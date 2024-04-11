package com.github.winexp.battlegrounds.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundEvents extends net.minecraft.sound.SoundEvents {
    public static final SoundEvent ENTITY_FLASH_BANG_REBOUND = register("entity.flash_bang.rebound");
    public static final SoundEvent PLAYER_TUBE_FALL = register("player.tube_fall");

    public static SoundEvent register(String name) {
        Identifier id = new Identifier("battlegrounds", name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSoundEvents() {
    }
}
