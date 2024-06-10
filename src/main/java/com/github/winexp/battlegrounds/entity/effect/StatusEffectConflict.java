package com.github.winexp.battlegrounds.entity.effect;

import net.minecraft.entity.effect.StatusEffectInstance;

public interface StatusEffectConflict {
    boolean isConflictWith(StatusEffectInstance that);
}
