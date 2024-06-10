package com.github.winexp.battlegrounds.mixin;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StatusEffectInstance.class)
public interface StatusEffectInstanceInvoker {
    @Invoker("<init>")
    static StatusEffectInstance invokeInit(RegistryEntry<StatusEffect> effect, StatusEffectInstance.Parameters parameters) {
        throw new AssertionError();
    }

    @Invoker("asParameters")
    StatusEffectInstance.Parameters invokeAsParameters();
}
