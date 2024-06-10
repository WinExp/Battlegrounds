package com.github.winexp.battlegrounds.mixin;

import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(StatusEffectInstance.Parameters.class)
public interface StatusEffectInstanceParametersInvoker {
    @Invoker("<init>")
    static StatusEffectInstance.Parameters invokeInit(int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, Optional<StatusEffectInstance.Parameters> hiddenEffect) {
        throw new AssertionError();
    }
}
