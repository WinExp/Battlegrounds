package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;

public class ButchersAxeItem extends AxeItem implements EnchantRestrict {
    private static final int PENALTY_DURATION = 3 * 20;

    public ButchersAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    private void givePenaltyEffects(LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, PENALTY_DURATION, 0), target);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, PENALTY_DURATION, 3), target);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, PENALTY_DURATION, 2), target);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, PENALTY_DURATION, 0), target);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, PENALTY_DURATION, 1), target);
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
            target.getWorld().playSound(target, target.getBlockPos(), SoundEvents.TUBE_FALL, SoundCategory.NEUTRAL, 0.5F, 1.0F);
        }
        return true;
    }
}
