package com.github.winexp.battlegrounds.enchantment;

import com.google.common.collect.ImmutableList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class StevesPainEnchantment extends Enchantment {
    private static final float EFFECT_CHANCE = 0.3F;
    private static final List<StatusEffectInstance> ATTACK_EFFECTS = ImmutableList.of(
            new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * 20, 1),
            new StatusEffectInstance(StatusEffects.WITHER, 5 * 20, 2),
            new StatusEffectInstance(StatusEffects.POISON, 5 * 20, 0),
            new StatusEffectInstance(StatusEffects.WEAKNESS, 5 * 20, 0)
    );

    public StevesPainEnchantment() {
        this(Enchantment.properties(
                ItemTags.SWORD_ENCHANTABLE,
                0, 1,
                Enchantment.leveledCost(10, 16),
                Enchantment.leveledCost(27, 16), 0
        ));
    }

    protected StevesPainEnchantment(Properties properties) {
        super(properties);
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        Random random = user.getRandom();
        if (random.nextFloat() <= EFFECT_CHANCE && target instanceof LivingEntity livingEntity) {
            for (StatusEffectInstance effectInstance : ATTACK_EFFECTS) {
                livingEntity.addStatusEffect(new StatusEffectInstance(effectInstance.getEffectType(), effectInstance.getDuration() * level, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.shouldShowParticles(), effectInstance.shouldShowIcon()), user);
            }
        }
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }
}
