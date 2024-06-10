package com.github.winexp.battlegrounds.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

public class AdrenalineStatusEffect extends StatusEffect implements StatusEffectConflict {
    protected AdrenalineStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean isConflictWith(StatusEffectInstance that) {
        return that.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL;
    }
}
