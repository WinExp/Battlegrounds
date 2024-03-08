package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.NbtCrafting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public class PVPProSwordItem extends SwordItem implements NbtCrafting, EnchantRestrict {
    public static final Identifier IDENTIFIER = new Identifier("battlegrounds", "pvp_pro_sword");
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.FIRE_ASPECT, 1,
            Enchantments.KNOCKBACK, 1,
            Enchantments.SWEEPING, 2,
            Enchantments.LOOTING, 3
    );

    public PVPProSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.getEquippedStack(EquipmentSlot.MAINHAND).isOf(this)) {
                this.addEffects(player);
            }
        }
    }

    private void addEffects(LivingEntity livingEntity) {
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 0));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2, 0));
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
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this, 1);
        ENCHANTMENTS.forEach(stack::addEnchantment);
        return stack;
    }

    @Override
    public ShapedRecipe getRecipe() {
        RawShapedRecipe rawShaped = RawShapedRecipe.create(Map.of(
                        'a', Ingredient.ofItems(Items.DIAMOND_BLOCK),
                        'b', Ingredient.ofItems(Items.GOLD_BLOCK),
                        'c', Ingredient.ofItems(Items.TOTEM_OF_UNDYING, Items.GOLDEN_APPLE),
                        'd', Ingredient.ofItems(Items.DIAMOND_SWORD),
                        'e', Ingredient.ofItems(Items.DIAMOND)
                ),
                "aba",
                "cdc",
                "ebe");
        return new ShapedRecipe(getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
