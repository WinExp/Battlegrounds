package com.github.winexp.battlegrounds.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;

public class Adrenaline extends StatusEffect {
    protected Adrenaline(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        ArrayList<StatusEffectInstance> effects = new ArrayList<>(entity.getStatusEffects());
        for (StatusEffectInstance effectInstance : effects) {
            StatusEffect effect = effectInstance.getEffectType();
            if (effect.getCategory() == StatusEffectCategory.HARMFUL && !effect.isInstant()) {
                entity.removeStatusEffect(effect);
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
