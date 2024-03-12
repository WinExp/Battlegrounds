package com.github.winexp.battlegrounds.item;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.item.recipe.NbtCrafting;
import com.github.winexp.battlegrounds.item.recipe.ShapedNbtCrafting;
import com.github.winexp.battlegrounds.item.recipe.ShapelessNbtCrafting;
import com.github.winexp.battlegrounds.item.thrown.FlashBangItem;
import com.github.winexp.battlegrounds.item.tool.*;
import com.github.winexp.battlegrounds.util.RecipeUtil;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Items extends net.minecraft.item.Items {
    public static final PVPProSwordItem PVP_PRO_SWORD = (PVPProSwordItem) register(PVPProSwordItem.IDENTIFIER, new PVPProSwordItem(ToolMaterials.PVP_PRO_SWORD, 3, -2.4F, new Item.Settings().rarity(Rarity.EPIC).fireproof()));
    public static final SevenElevenSwordItem SEVEN_ELEVEN_SWORD = (SevenElevenSwordItem) register(SevenElevenSwordItem.IDENTIFIER, new SevenElevenSwordItem(ToolMaterials.SEVEN_ELEVEN_SWORD, 3, -2.4F, new Item.Settings().rarity(Rarity.EPIC).fireproof()));
    public static final StevesPainSwordItem STEVES_PAIN_SWORD = (StevesPainSwordItem) register(StevesPainSwordItem.IDENTIFIER, new StevesPainSwordItem(ToolMaterials.STEVES_PAIN_SWORD, 3, -2.4F, new Item.Settings().rarity(Rarity.EPIC).fireproof()));
    public static final LeachingSwordItem LEACHING_SWORD = (LeachingSwordItem) register(LeachingSwordItem.IDENTIFIER, new LeachingSwordItem(ToolMaterials.LEACHING_SWORD, 3, -2.4F, new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final MinersPickaxeItem MINERS_PICKAXE = (MinersPickaxeItem) register(MinersPickaxeItem.IDENTIFIER, new MinersPickaxeItem(ToolMaterials.MINERS_PICKAXE, 1, -2.8F, new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final ButchersAxeItem BUTCHERS_AXE = (ButchersAxeItem) register(ButchersAxeItem.IDENTIFIER, new ButchersAxeItem(ToolMaterials.BUTCHERS_AXE, 5, -3.0F, new Item.Settings().rarity(Rarity.EPIC).fireproof()));
    public static final ChannelingBowItem CHANNELING_BOW = (ChannelingBowItem) register(ChannelingBowItem.IDENTIFIER, new ChannelingBowItem(new Item.Settings().rarity(Rarity.RARE).fireproof().maxDamage(ChannelingBowItem.DURABILITY)));
    public static final FlashBangItem FLASH_BANG = (FlashBangItem) register(FlashBangItem.IDENTIFIER, new FlashBangItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));
    public static final RupertsTearItem RUPERTS_TEAR = (RupertsTearItem) register(RupertsTearItem.IDENTIFIER, new RupertsTearItem(ToolMaterials.RUPERTS_TEAR, new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final KnockbackStickItem KNOCKBACK_STICK = (KnockbackStickItem) register(KnockbackStickItem.IDENTIFIER, new KnockbackStickItem(ToolMaterials.KNOCKBACK_STICK, new Item.Settings().maxCount(1).rarity(Rarity.RARE)));

    private static void registerDispenserBehavior() {
        DispenserBlock.registerBehavior(FLASH_BANG, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new FlashBangEntity(world, position.getX(), position.getY(), position.getZ()), (entity) -> {
                    entity.setItem(stack);
                    entity.setFuse(FlashBangItem.getFuse(stack.getNbt()));
                });
            }
        });
    }

    private static void registerModelPredicate() {
        ModelPredicateProviderRegistry.register(CHANNELING_BOW, new Identifier("pull"), (stack, clientWorld, entity, seed) -> {
            if (entity == null) return 0.0F;
            return entity.getActiveItem() != stack ? 0.0F : (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
        });
        ModelPredicateProviderRegistry.register(CHANNELING_BOW, new Identifier("pulling"), (stack, clientWorld, entity, seed) -> {
            if (entity == null) return 0.0F;
            return entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
        });
    }

    public static void registerItems() {
        registerDispenserBehavior();
        registerModelPredicate();
    }

    public static void addRecipes() {
        List<ShapedNbtCrafting> shapedRecipes = List.of(
                // 自动冶炼
                new ShapedNbtCrafting(
                        new Identifier("battlegrounds", "enchanted_book_smelting"),
                        RawShapedRecipe.create(Map.of(
                                        'a', Ingredient.ofItems(FURNACE),
                                        'b', Ingredient.ofItems(STONE_PICKAXE),
                                        'c', Ingredient.ofItems(RAW_IRON)
                                ), "aba",
                                "bcb",
                                "aba"),
                        CraftingRecipeCategory.EQUIPMENT,
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(
                                Enchantments.SMELTING, 1
                        ))
                ),
                // 铁镐（自动冶炼）
                new ShapedNbtCrafting(
                        new Identifier("battlegrounds", "iron_pickaxe_smelting"),
                        RawShapedRecipe.create(Map.of(
                                        'a', Ingredient.ofItems(DIAMOND),
                                        'b', Ingredient.ofItems(LIGHTNING_ROD),
                                        'c', Ingredient.ofItems(Items.BOOK)
                                ), "aaa",
                                "bcb",
                                " c "),
                        CraftingRecipeCategory.EQUIPMENT,
                        Util.make(IRON_PICKAXE.getDefaultStack(), (stack) ->
                                stack.addEnchantment(Enchantments.SMELTING, 1))
                ),
                // 钻石胸甲（生机勃勃 3）
                new ShapedNbtCrafting(
                        new Identifier("battlegrounds", "diamond_chestplate_vitality"),
                        RawShapedRecipe.create(Map.of(
                                        'a', Ingredient.ofItems(DIAMOND),
                                        'b', Ingredient.ofItems(LIGHT),
                                        'c', Ingredient.ofItems(BOOK)
                                ), "aaa",
                                "bcb",
                                " c "),
                        CraftingRecipeCategory.EQUIPMENT,
                        Util.make(DIAMOND_CHESTPLATE.getDefaultStack(), (stack) ->
                                stack.addEnchantment(Enchantments.VITALITY, 3))
                )
        );
        List<ShapelessNbtCrafting> shapelessRecipes = List.of(
                // 锋利 1 附魔书
                new ShapelessNbtCrafting(
                        new Identifier("battlegrounds", "enchanted_book_sharpness_1"),
                        CraftingRecipeCategory.EQUIPMENT,
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(
                                Enchantments.SHARPNESS, 1
                        )),
                        List.of(
                                Ingredient.ofItems(IRON_SWORD),
                                Ingredient.ofItems(BOOK)
                        )
                ),
                // 保护 1 附魔书
                new ShapelessNbtCrafting(
                        new Identifier("battlegrounds", "enchanted_book_protection_1"),
                        CraftingRecipeCategory.EQUIPMENT,
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(
                                Enchantments.PROTECTION, 1
                        )),
                        List.of(
                                Ingredient.ofItems(IRON_CHESTPLATE),
                                Ingredient.ofItems(BOOK)
                        )
                ),
                // 力量 1 附魔书
                new ShapelessNbtCrafting(
                        new Identifier("battlegrounds", "enchanted_book_power_1"),
                        CraftingRecipeCategory.EQUIPMENT,
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(
                                Enchantments.POWER, 1
                        )),
                        List.of(
                                Ingredient.ofItems(BOW),
                                Ingredient.ofItems(BOOK)
                        )
                )
        );

        ArrayList<NbtCrafting> items = new ArrayList<>(List.of(
                PVP_PRO_SWORD, SEVEN_ELEVEN_SWORD, STEVES_PAIN_SWORD, LEACHING_SWORD,
                MINERS_PICKAXE,
                CHANNELING_BOW,
                KNOCKBACK_STICK
        ));
        items.addAll(shapedRecipes);
        items.addAll(shapelessRecipes);
        for (NbtCrafting item : items) {
            RecipeEntry<CraftingRecipe> entry = new RecipeEntry<>(
                    item.getIdentifier(),
                    item.getRecipe()
            );
            RecipeUtil.addRecipe(entry);
        }
    }
}
