package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;

import java.util.List;

public class ButchersAxeItem extends AxeItem implements EnchantRestrict {
    private static final List<StatusEffectInstance> penaltyEffects = List.of(
            new StatusEffectInstance(StatusEffects.SLOWNESS, 7 * 20, 1),
            new StatusEffectInstance(StatusEffects.WITHER, 7 * 20, 0),
            new StatusEffectInstance(StatusEffects.POISON, 7 * 20, 0),
            new StatusEffectInstance(StatusEffects.WEAKNESS, 7 * 20, 0),
            new StatusEffectInstance(StatusEffects.APPROACHING_EXTINCTION, 7 * 20, 0)
    );

    public ButchersAxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    private void givePenaltyEffects(LivingEntity target) {
        for (StatusEffectInstance effectInstance : penaltyEffects) {
            target.addStatusEffect(new StatusEffectInstance(effectInstance));
        }
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
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
        if (!target.getWorld().isClient) {
            this.givePenaltyEffects(attacker);
            target.playSound(SoundEvents.PLAYER_TUBE_FALL, 2.0F, 1.0F);
        }
        return true;
    }
}
