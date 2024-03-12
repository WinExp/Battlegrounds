package com.github.winexp.battlegrounds.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundEvents extends net.minecraft.sound.SoundEvents {
    public static final SoundEvent TUBE_FALL = register("tube_fall");

    public static SoundEvent register(String name) {
        Identifier id = new Identifier("battlegrounds", name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSoundEvents() {
    }
}
