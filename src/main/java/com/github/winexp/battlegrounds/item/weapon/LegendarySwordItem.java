package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LegendarySwordItem extends SwordItem {
    private final List<StatusEffectInstance> enrichEffects;
    private final List<StatusEffectInstance> attackEffects;
    private final int attackEffectBound;
    private final boolean enchantable;
    private final boolean hasGlint;

    public LegendarySwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
        this.enrichEffects = settings.enrichEffects;
        this.attackEffects = settings.attackEffects;
        this.attackEffectBound = settings.attackEffectBound;
        this.enchantable = settings.enchantable;
        this.hasGlint = settings.hasGlint;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof LivingEntity livingEntity
                && selected && (livingEntity.getEquippedStack(EquipmentSlot.MAINHAND).isOf(this)
                || livingEntity.getEquippedStack(EquipmentSlot.OFFHAND).isOf(this))) {
            for (StatusEffectInstance statusEffectInstance : this.enrichEffects) {
                livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance), livingEntity);
            }
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return this.enchantable;
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, Enchantment enchantment, EnchantingContext context) {
        return this.enchantable;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return this.hasGlint;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.battlegrounds.legendary")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
        Random random = attacker.getRandom();
        if (random.nextBetween(1, 100) <= this.attackEffectBound) {
            for (StatusEffectInstance statusEffectInstance : attackEffects) {
                target.addStatusEffect(new StatusEffectInstance(statusEffectInstance), attacker);
            }
        }
        return true;
    }

    public static class Settings extends Item.Settings {
        private final List<StatusEffectInstance> enrichEffects = new ArrayList<>(ImmutableList.of(
                new StatusEffectInstance(StatusEffects.RESISTANCE, 2 * 20, 0)
        ));
        private final List<StatusEffectInstance> attackEffects = new ArrayList<>(ImmutableList.of(
                new StatusEffectInstance(StatusEffects.APPROACHING_EXTINCTION, 15 * 20, 0)
        ));
        private int attackEffectBound = 100;
        private boolean enchantable = false;
        private boolean hasGlint = false;

        public Settings() {
            super();
        }

        @Override
        public Settings equipmentSlot(EquipmentSlotProvider equipmentSlotProvider) {
            super.equipmentSlot(equipmentSlotProvider);
            return this;
        }

        @Override
        public Settings customDamage(CustomDamageHandler handler) {
            super.customDamage(handler);
            return this;
        }

        @Override
        public Settings food(FoodComponent foodComponent) {
            super.food(foodComponent);
            return this;
        }

        @Override
        public Settings maxCount(int maxCount) {
            super.maxCount(maxCount);
            return this;
        }

        @Override
        public Settings maxDamage(int maxDamage) {
            super.maxDamage(maxDamage);
            return this;
        }

        @Override
        public Settings recipeRemainder(Item recipeRemainder) {
            super.recipeRemainder(recipeRemainder);
            return this;
        }

        @Override
        public Settings rarity(Rarity rarity) {
            super.rarity(rarity);
            return this;
        }

        @Override
        public Settings fireproof() {
            super.fireproof();
            return this;
        }

        @Override
        public Settings requires(FeatureFlag... features) {
            super.requires(features);
            return this;
        }

        @Override
        public <T> Settings component(DataComponentType<T> type, T value) {
            super.component(type, value);
            return this;
        }

        @Override
        public Settings attributeModifiers(AttributeModifiersComponent attributeModifiersComponent) {
            super.attributeModifiers(attributeModifiersComponent);
            return this;
        }

        public Settings ignoreEnrichEffects() {
            this.enrichEffects.clear();
            return this;
        }

        public Settings enrichEffect(StatusEffectInstance statusEffectInstance) {
            this.enrichEffects.add(statusEffectInstance);
            return this;
        }

        public Settings ignoreAttackEffects() {
            this.attackEffects.clear();
            return this;
        }

        public Settings attackEffect(StatusEffectInstance statusEffectInstance) {
            this.attackEffects.add(statusEffectInstance);
            return this;
        }

        public Settings attackEffectBound(int bound) {
            this.attackEffectBound = bound;
            return this;
        }

        public Settings glint() {
            this.hasGlint = true;
            return this;
        }

        public Settings enchantable() {
            this.enchantable = true;
            return this;
        }
    }
}
