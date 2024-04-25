package com.github.winexp.battlegrounds.enchantment;

import com.github.winexp.battlegrounds.util.EntityUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class VitalityEnchantment extends Enchantment {
    private final static Identifier HEALTH_MODIFIER_ID = new Identifier("battlegrounds", "enchantment/vitality");
    private final static double HEALTH_MODIFIER_ADD_VALUE_PER_LEVEL = 4;

    public VitalityEnchantment() {
        this(Enchantment.properties(
                ItemTags.CHEST_ARMOR_ENCHANTABLE,
                0, 3,
                Enchantment.leveledCost(7, 12),
                Enchantment.leveledCost(19, 12), 0
        ));
    }

    protected VitalityEnchantment(Properties properties) {
        super(properties);
        ServerEntityEvents.EQUIPMENT_CHANGE.register(this::onEquipmentChange);
    }

    private void onEquipmentChange(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack) {
        if (equipmentSlot == EquipmentSlot.CHEST) {
            int level = EnchantmentHelper.getLevel(Enchantments.VITALITY, currentStack);
            this.modifyHealth(livingEntity, level);
        }
    }

    private void modifyHealth(LivingEntity livingEntity, int level) {
        EntityAttributeInstance attribute = livingEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        assert attribute != null;
        if (level > 0) {
            EntityUtil.addAttributeModifier(attribute, HEALTH_MODIFIER_ID,
                    level * HEALTH_MODIFIER_ADD_VALUE_PER_LEVEL, EntityAttributeModifier.Operation.ADD_VALUE);
        } else {
            EntityUtil.removeAttributeModifier(attribute, HEALTH_MODIFIER_ID);
        }
    }

    @Override
    public boolean isTreasure() {
        return true;
    }
}
