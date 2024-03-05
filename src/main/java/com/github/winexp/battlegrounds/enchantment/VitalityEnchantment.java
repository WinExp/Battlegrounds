package com.github.winexp.battlegrounds.enchantment;

import com.github.winexp.battlegrounds.util.EffectUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;

public class VitalityEnchantment extends Enchantment {
    private final String healthModifierId = "enchantment.vitality.health_modifier";

    public VitalityEnchantment() {
        this(Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST, EquipmentSlot.CHEST);
    }

    protected VitalityEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot... slots) {
        super(rarity, target, slots);
        ServerEntityEvents.EQUIPMENT_CHANGE.register(this::onEquipmentChange);
    }

    private void onEquipmentChange(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack) {
        if (equipmentSlot == EquipmentSlot.CHEST) {
            int level = EnchantmentHelper.getLevel(Enchantments.VITALITY, currentStack);
            this.modifyHealth(livingEntity, level, level > 0);
        }
    }

    private void modifyHealth(LivingEntity livingEntity, int level, boolean addition) {
        EntityAttributeInstance attribute = livingEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        assert attribute != null;
        if (addition) {
            EffectUtil.addAttribute(attribute, healthModifierId,
                    level * 4, EntityAttributeModifier.Operation.ADDITION);
        } else {
            EffectUtil.removeAttribute(attribute, healthModifierId);
        }
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinPower(int level) {
        return 30;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMaxPower(level) + 40;
    }
}
