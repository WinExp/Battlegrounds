package com.github.winexp.battlegrounds.enchantment;

import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.google.common.collect.ImmutableList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.tag.ItemTags;

import java.util.List;

public class LeachingEnchantment extends Enchantment {
    private static final List<StatusEffectInstance> ATTACK_EFFECTS = ImmutableList.of(
            new StatusEffectInstance(StatusEffects.POISON, 5 * 20, 3),
            new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * 20, 0),
            new StatusEffectInstance(StatusEffects.HUNGER, 5 * 20, 2),
            new StatusEffectInstance(StatusEffects.NAUSEA, 5 * 20, 0)
    );

    public LeachingEnchantment() {
        this(Enchantment.properties(
                ItemTags.SWORD_ENCHANTABLE,
                0, 1,
                Enchantment.leveledCost(1, 9),
                Enchantment.leveledCost(19, 9), 1
        ));
    }

    protected LeachingEnchantment(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity livingEntity) {
            for (StatusEffectInstance effectInstance : ATTACK_EFFECTS) {
                livingEntity.addStatusEffect(new StatusEffectInstance(effectInstance.getEffectType(), effectInstance.getDuration() * level, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.shouldShowParticles(), effectInstance.shouldShowIcon()), user);
            }
        }
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
