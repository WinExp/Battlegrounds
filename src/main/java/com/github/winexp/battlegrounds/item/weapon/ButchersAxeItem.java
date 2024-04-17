package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;

import java.util.Map;

public class ButchersAxeItem extends AxeItem implements EnchantRestrict {
    private static final int PENALTY_DURATION = 7 * 20;
    private static final Map<StatusEffect, Integer> penaltyEffects = Map.of(
            StatusEffects.SLOWNESS, 1,
            StatusEffects.WITHER, 0,
            StatusEffects.POISON, 0,
            StatusEffects.WEAKNESS, 0,
            StatusEffects.APPROACHING_EXTINCTION, 0
    );

    public ButchersAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    private void givePenaltyEffects(LivingEntity target) {
        penaltyEffects.forEach((effect, amplifier) ->
                target.addStatusEffect(new StatusEffectInstance(effect, PENALTY_DURATION, amplifier), target));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendToolBreakStatus(Hand.MAIN_HAND));
        if (!target.getWorld().isClient) {
            this.givePenaltyEffects(attacker);
            target.playSound(SoundEvents.PLAYER_TUBE_FALL, 2.0F, 1.0F);
        }
        return true;
    }
}
