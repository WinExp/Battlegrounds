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
    public static final PVPProSwordItem PVP_PRO_SWORD = (PVPProSwordItem) register(PVPProSwordItem.IDENTIFIER, new PVPProSwordItem(ToolMaterials.PVP_PRO_SWORD, 3, -2.4F, new Item.Settings().rarity(Rarity.RARE).fireproof()));
    public static final SevenElevenSwordItem SEVEN_ELEVEN_SWORD = (SevenElevenSwordItem) register(SevenElevenSwordItem.IDENTIFIER, new SevenElevenSwordItem(ToolMaterials.SEVEN_ELEVEN_SWORD, 3, -2.4F, new Item.Settings().rarity(Rarity.RARE).fireproof()));
    public static final MinersPickaxeItem MINERS_PICKAXE = (MinersPickaxeItem) register(MinersPickaxeItem.IDENTIFIER, new MinersPickaxeItem(ToolMaterials.MINERS_PICKAXE, 1, -2.8F, new Item.Settings().rarity(Rarity.RARE)));
    public static final FlashBangItem FLASH_BANG = (FlashBangItem) register(FlashBangItem.IDENTIFIER, new FlashBangItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));
    public static final RupertsTearItem RUPERTS_TEAR = (RupertsTearItem) register(RupertsTearItem.IDENTIFIER, new RupertsTearItem(ToolMaterials.RUPERTS_TEAR, new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));

    private static void registerDispenserBehavior() {
        DispenserBlock.registerBehavior(Items.FLASH_BANG, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new FlashBangEntity(world, position.getX(), position.getY(), position.getZ()), (entity) -> {
                    entity.setItem(stack);
                    if (stack.getNbt() != null && stack.getNbt().contains("fuse")) {
                        entity.setFuse(stack.getNbt().getInt("fuse"));
                    }
                });
            }
        });
    }

    public static void registerItems() {
        registerDispenserBehavior();
    }

    public static void addRecipes() {
        List<ShapedNbtCrafting> shapedRecipes = List.of(
                // 自动冶炼
                new ShapedNbtCrafting(
                        new Identifier("battlegrounds", "enchanted_book_smelting"),
                        RawShapedRecipe.create(Map.of(
                                        'a', Ingredient.ofItems(Items.FURNACE),
                                        'b', Ingredient.ofItems(Items.STONE_PICKAXE),
                                        'c', Ingredient.ofItems(Items.RAW_IRON)
                                ), "aba",
                                "bcb",
                                "aba"),
                        CraftingRecipeCategory.EQUIPMENT,
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(
                                Enchantments.SMELTING, 1
                        ))
                ),
                // 引雷 Pro Max
                new ShapedNbtCrafting(
                        new Identifier("battlegrounds", "enchanted_book_channeling_pro"),
                        RawShapedRecipe.create(Map.of(
                                        'a', Ingredient.ofItems(Items.DIAMOND),
                                        'b', Ingredient.ofItems(Items.LIGHTNING_ROD),
                                        'c', Ingredient.ofItems(Items.BOOK)
                                ), "aba",
                                "aca",
                                "aba"),
                        CraftingRecipeCategory.EQUIPMENT,
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(
                                Enchantments.CHANNELING_PRO, 1
                        ))
                ),
                // 铁镐（自动冶炼）
                new ShapedNbtCrafting(
                        new Identifier("battlegrounds", "iron_pickaxe_smelting"),
                        RawShapedRecipe.create(Map.of(
                                        'a', Ingredient.ofItems(Items.DIAMOND),
                                        'b', Ingredient.ofItems(Items.LIGHTNING_ROD),
                                        'c', Ingredient.ofItems(Items.BOOK)
                                ), "aaa",
                                "bcb",
                                " c "),
                        CraftingRecipeCategory.EQUIPMENT,
                        Util.make(new ItemStack(Items.IRON_PICKAXE, 1), (stack) ->
                                stack.addEnchantment(Enchantments.SMELTING, 1))
                ),
                // 史蒂夫の痛
                new ShapedNbtCrafting(
                        new Identifier("battlegrounds", "enchanted_book_steves_pain"),
                        RawShapedRecipe.create(Map.of(
                                        'a', Ingredient.ofItems(Items.DIAMOND_BLOCK),
                                        'b', Ingredient.ofItems(Items.LAPIS_LAZULI),
                                        'c', Ingredient.ofItems(Items.TOTEM_OF_UNDYING),
                                        'd', Ingredient.ofItems(Items.ZOMBIE_HEAD),
                                        'e', Ingredient.ofItems(Items.DIAMOND_SWORD)
                                ), "aba",
                                "ced",
                                "aba"),
                        CraftingRecipeCategory.EQUIPMENT,
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(
                                Enchantments.STEVES_PAIN, 1
                        ))
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
                                Ingredient.ofItems(Items.IRON_SWORD),
                                Ingredient.ofItems(Items.BOOK)
                        )
                ),
                // 浸毒 附魔书
                new ShapelessNbtCrafting(
                        new Identifier("battlegrounds", "enchanted_book_leaching"),
                        CraftingRecipeCategory.EQUIPMENT,
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(
                                Enchantments.LEACHING, 1
                        )),
                        List.of(
                                Ingredient.ofItems(Items.BOOK),
                                Ingredient.ofItems(Items.SPIDER_EYE),
                                Ingredient.ofItems(Items.SUGAR),
                                Ingredient.ofItems(Items.EMERALD)
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
                                Ingredient.ofItems(Items.IRON_CHESTPLATE),
                                Ingredient.ofItems(Items.BOOK)
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
                                Ingredient.ofItems(Items.BOW),
                                Ingredient.ofItems(Items.BOOK)
                        )
                )
        );

        ArrayList<NbtCrafting> items = new ArrayList<>(List.of(
                PVP_PRO_SWORD, SEVEN_ELEVEN_SWORD, MINERS_PICKAXE
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
