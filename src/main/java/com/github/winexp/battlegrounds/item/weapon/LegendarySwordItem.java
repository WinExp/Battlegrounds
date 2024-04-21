package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtRecipe;
import com.github.winexp.battlegrounds.item.recipe.NbtRecipe;
import com.github.winexp.battlegrounds.item.tool.TooltipProvider;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LegendarySwordItem extends SwordItem implements ItemNbtRecipe, EnchantRestrict {
    private final List<StatusEffectInstance> enrichEffects;
    private final List<StatusEffectInstance> attackEffects;
    private final int attackEffectBound;
    private final Supplier<ItemStack> defaultStackProvider;
    private final Supplier<NbtRecipe> recipeProvider;
    private final boolean enchantable;
    private final boolean grindable;
    private final boolean hasGlint;
    private final List<TooltipProvider> tooltipProviders;

    public LegendarySwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.enrichEffects = settings.enrichEffects;
        this.attackEffects = settings.attackEffects;
        this.attackEffectBound = settings.attackEffectBound;
        this.defaultStackProvider = settings.defaultStackProvider;
        this.recipeProvider = settings.recipeProvider;
        this.enchantable = settings.enchantable;
        this.grindable = settings.grindable;
        this.hasGlint = settings.hasGlint;
        this.tooltipProviders = settings.tooltipProviders;
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.getEquippedStack(EquipmentSlot.MAINHAND).isOf(this)
            || player.getEquippedStack(EquipmentSlot.OFFHAND).isOf(this)) {
                for (StatusEffectInstance statusEffectInstance : this.enrichEffects) {
                    player.addStatusEffect(new StatusEffectInstance(statusEffectInstance), player);
                }
            }
        }
    }

    @Override
    public boolean isEnchantable(Enchantment enchantment, EnchantmentTarget target) {
        return this.enchantable;
    }

    @Override
    public boolean isGrindable(ItemStack stack) {
        return this.grindable;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = this.defaultStackProvider.get();
        return stack == ItemStack.EMPTY ? super.getDefaultStack() : stack;
    }

    @Override
    public CraftingRecipe getRecipe() {
        return this.recipeProvider.get().getRecipe();
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return this.hasGlint;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.battlegrounds.legendary")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
        for (TooltipProvider tooltipProvider : this.tooltipProviders) {
            tooltip.add(tooltipProvider.getTooltip(stack, world, context));
        }
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

    public static class Settings extends FabricItemSettings {
        private final List<StatusEffectInstance> enrichEffects;
        private final List<StatusEffectInstance> attackEffects;
        private int attackEffectBound;
        private Supplier<ItemStack> defaultStackProvider;
        private Supplier<NbtRecipe> recipeProvider;
        private boolean enchantable;
        private boolean grindable;
        private boolean hasGlint;
        private final List<TooltipProvider> tooltipProviders;

        public Settings() {
            super();
            this.enrichEffects = new ArrayList<>(List.of(
                    new StatusEffectInstance(StatusEffects.RESISTANCE, 0, 2 * 20)
            ));
            this.attackEffects = new ArrayList<>(List.of(
                    new StatusEffectInstance(StatusEffects.APPROACHING_EXTINCTION, 0, 15 * 20)
            ));
            this.attackEffectBound = 100;
            this.defaultStackProvider = () -> ItemStack.EMPTY;
            this.recipeProvider = () -> NbtRecipe.EMPTY;
            this.enchantable = false;
            this.grindable = false;
            this.hasGlint = false;
            this.tooltipProviders = new ArrayList<>();
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
        public Settings maxDamageIfAbsent(int maxDamage) {
            super.maxDamageIfAbsent(maxDamage);
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

        public Settings defaultStack(Supplier<ItemStack> itemStackProvider) {
            this.defaultStackProvider = itemStackProvider;
            return this;
        }

        public Settings recipe(Supplier<NbtRecipe> recipeProvider) {
            this.recipeProvider = recipeProvider;
            this.defaultStackProvider = () -> recipeProvider.get().getDefaultStack();
            return this;
        }

        public Settings enchantable() {
            this.enchantable = true;
            return this;
        }

        public Settings grindable() {
            this.grindable = true;
            return this;
        }

        public Settings glint() {
            this.hasGlint = true;
            return this;
        }

        public Settings tooltip(TooltipProvider tooltipProvider) {
            this.tooltipProviders.add(tooltipProvider);
            return this;
        }

        public Settings tooltip(Text text) {
            return this.tooltip((stack, world, context) -> text);
        }
    }
}
