package com.github.winexp.battlegrounds.entity.effect;

import net.minecraft.entity.effect.StatusEffect;

public record StatusEffectEntry(StatusEffect statusEffect, int amplifier, int duration) {
}
