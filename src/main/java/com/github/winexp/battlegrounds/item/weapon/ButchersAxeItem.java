package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;

import java.util.List;

public class ButchersAxeItem extends AxeItem {
    private static final List<StatusEffectInstance> penaltyEffects = ImmutableList.of(
            new StatusEffectInstance(StatusEffects.SLOWNESS, 7 * 20, 1),
            new StatusEffectInstance(StatusEffects.WITHER, 7 * 20, 0),
            new StatusEffectInstance(StatusEffects.POISON, 7 * 20, 0),
            new StatusEffectInstance(StatusEffects.WEAKNESS, 7 * 20, 0),
            new StatusEffectInstance(StatusEffects.APPROACHING_EXTINCTION, 7 * 20, 0)
    );

    public ButchersAxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
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
    public boolean canBeEnchantedWith(ItemStack stack, Enchantment enchantment, EnchantingContext context) {
        return false;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
        if (!target.getWorld().isClient) {
            for (StatusEffectInstance effectInstance : penaltyEffects) {
                attacker.addStatusEffect(new StatusEffectInstance(effectInstance));
            }
            target.playSound(SoundEvents.PLAYER_TUBE_FALL, 2.0F, 1.0F);
        }
        return true;
    }
}
