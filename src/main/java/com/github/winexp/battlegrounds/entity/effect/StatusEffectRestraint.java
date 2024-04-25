package com.github.winexp.battlegrounds.entity.effect;

import net.minecraft.entity.effect.StatusEffectInstance;

public interface StatusEffectRestraint {
    boolean isStatusEffectAllowed(StatusEffectInstance statusEffectInstance);
}
