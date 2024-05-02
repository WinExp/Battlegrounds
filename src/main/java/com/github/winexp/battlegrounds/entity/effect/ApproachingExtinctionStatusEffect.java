package com.github.winexp.battlegrounds.entity.effect;

import com.github.winexp.battlegrounds.event.ModServerPlayerEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;

public class ApproachingExtinctionStatusEffect extends StatusEffect {
    protected ApproachingExtinctionStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
        ModServerPlayerEvents.ALLOW_NATURAL_REGEN.register(this::allowPlayerNaturalRegen);
    }

    private boolean allowPlayerNaturalRegen(ServerPlayerEntity player) {
        return !player.hasStatusEffect(this) || player.hasStatusEffect(StatusEffects.ADRENALINE);
    }
}
