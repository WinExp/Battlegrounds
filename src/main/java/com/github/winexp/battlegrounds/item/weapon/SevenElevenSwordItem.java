package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtCrafting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SevenElevenSwordItem extends SwordItem implements ItemNbtCrafting, EnchantRestrict {
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.SWEEPING, 3,
            Enchantments.LOOTING, 3
    );
    private static final int BOUND = 30;
    private static final int EFFECT_DURATION = 80;

    public SevenElevenSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
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

    private void giveEffects(LivingEntity attacker, LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, EFFECT_DURATION, 1), attacker);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, EFFECT_DURATION, 0), attacker);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
        if (!attacker.getWorld().isClient) {
            Random random = attacker.getRandom();
            if (random.nextInt(100) + 1 <= BOUND) {
                this.giveEffects(attacker, target);
            }
        }
        return true;
    }

    private void addEffects(LivingEntity livingEntity) {
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 0));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 2, 0));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2, 0));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 2, 0));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.battlegrounds.seven_eleven_sword.tooltip.1")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
        tooltip.add(Text.translatable("item.battlegrounds.seven_eleven_sword.tooltip.2")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
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
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this, 1);
        ENCHANTMENTS.forEach(stack::addEnchantment);
        return stack;
    }

    @Override
    public ShapedRecipe getRecipe() {
        RawShapedRecipe rawShaped = RawShapedRecipe.create(Map.of(
                        'a', Ingredient.ofItems(Items.DIAMOND),
                        'b', Ingredient.ofItems(Items.AMETHYST_SHARD),
                        'c', Ingredient.ofStacks(PotionUtil.setPotion(Items.SPLASH_POTION.getDefaultStack(), Potions.TURTLE_MASTER)),
                        'd', Ingredient.ofItems(Items.NETHERITE_SWORD),
                        'e', Ingredient.ofStacks(PotionUtil.setPotion(Items.SPLASH_POTION.getDefaultStack(), Potions.STRONG_SWIFTNESS)),
                        'f', Ingredient.ofItems(Items.DIAMOND_BLOCK),
                        'g', Ingredient.ofItems(Items.EMERALD)
                ),
                "aba",
                "cde",
                "fgf");
        return new ShapedRecipe(this.getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
