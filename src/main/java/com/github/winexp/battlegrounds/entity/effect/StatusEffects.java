package com.github.winexp.battlegrounds.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class StatusEffects extends net.minecraft.entity.effect.StatusEffects {
    public static final RegistryEntry<StatusEffect> ADRENALINE = register("adrenaline",
            new AdrenalineStatusEffect(StatusEffectCategory.BENEFICIAL, ColorHelper.Argb.getArgb(255, 120, 180, 20)));
    public static final RegistryEntry<StatusEffect> APPROACHING_EXTINCTION = register("approaching_extinction",
            new ApproachingExtinctionStatusEffect(StatusEffectCategory.HARMFUL, ColorHelper.Argb.getArgb(255, 180, 80, 80)));

    private static <T extends StatusEffect> RegistryEntry<StatusEffect> register(String id, T effect) {
        return Registry.registerReference(
                Registries.STATUS_EFFECT,
                new Identifier("battlegrounds", id),
                effect);
    }

    public static void bootstrap() {
    }
}
