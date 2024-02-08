package com.github.winexp.battlegrounds.item;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.recipe.NBTCrafting;
import com.github.winexp.battlegrounds.item.recipe.ShapedNBTCrafting;
import com.github.winexp.battlegrounds.item.recipe.ShapelessNBTCrafting;
import com.github.winexp.battlegrounds.item.tool.MinersPickaxeItem;
import com.github.winexp.battlegrounds.item.tool.PVPProSwordItem;
import com.github.winexp.battlegrounds.item.tool.ToolMaterials;
import com.github.winexp.battlegrounds.util.RecipeUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Items extends net.minecraft.item.Items {
    public final static PVPProSwordItem PVP_PRO_SWORD = new PVPProSwordItem(ToolMaterials.PVP_PRO, 3, -2.4F, new Item.Settings().rarity(Rarity.RARE).fireproof());
    public final static MinersPickaxeItem MINERS_PICKAXE = new MinersPickaxeItem(ToolMaterials.MINERS_PICKAXE, 1, -2.8F, new Item.Settings().rarity(Rarity.RARE));

    public static void registerItems() {
        Registry.register(Registries.ITEM,
                PVP_PRO_SWORD.getIdentifier(),
                PVP_PRO_SWORD
        );
        Registry.register(Registries.ITEM,
                MINERS_PICKAXE.getIdentifier(),
                MINERS_PICKAXE
        );
    }

    public static void registerItemGroup() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(PVP_PRO_SWORD.getItemStack());
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(MINERS_PICKAXE.getItemStack());
        });
    }

    public static void addRecipes() {
        List<ShapedNBTCrafting> shapedRecipes = List.of(
                // 自动冶炼
                new ShapedNBTCrafting(
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
                new ShapedNBTCrafting(
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
                )
        );
        List<ShapelessNBTCrafting> shapelessRecipes = List.of(
                // 锋利 1 附魔书
                new ShapelessNBTCrafting(
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
                new ShapelessNBTCrafting(
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
                new ShapelessNBTCrafting(
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
                new ShapelessNBTCrafting(
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

        ArrayList<NBTCrafting> items = new ArrayList<>(List.of(
                PVP_PRO_SWORD, MINERS_PICKAXE
        ));
        items.addAll(shapedRecipes);
        items.addAll(shapelessRecipes);
        for (NBTCrafting item : items) {
            RecipeEntry<CraftingRecipe> entry = new RecipeEntry<>(
                    item.getIdentifier(),
                    item.getRecipe()
            );
            RecipeUtil.addRecipe(entry);
        }
    }
}
