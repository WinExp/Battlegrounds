package com.github.winexp.battlegrounds.item;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.item.recipe.NbtCrafting;
import com.github.winexp.battlegrounds.item.recipe.ShapedNbtCrafting;
import com.github.winexp.battlegrounds.item.recipe.ShapelessNbtCrafting;
import com.github.winexp.battlegrounds.item.thrown.FlashBangItem;
import com.github.winexp.battlegrounds.item.tool.MinersPickaxeItem;
import com.github.winexp.battlegrounds.item.tool.PVPProSwordItem;
import com.github.winexp.battlegrounds.item.tool.ToolMaterials;
import com.github.winexp.battlegrounds.util.RecipeUtil;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Items extends net.minecraft.item.Items {
    public final static PVPProSwordItem PVP_PRO_SWORD = new PVPProSwordItem(ToolMaterials.PVP_PRO, 3, -2.4F, new Item.Settings().rarity(Rarity.RARE).fireproof());
    public final static MinersPickaxeItem MINERS_PICKAXE = new MinersPickaxeItem(ToolMaterials.MINERS_PICKAXE, 1, -2.8F, new Item.Settings().rarity(Rarity.RARE));
    public final static FlashBangItem FLASH_BANG = new FlashBangItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON));

    public final static Predicate<Item> ENCHANTMENT_PREDICATE = (item) -> {
        if (item instanceof PVPProSwordItem) return false;
        else if (item instanceof MinersPickaxeItem) return false;
        else return true;
    };

    public static void registerItems() {
        Registry.register(Registries.ITEM,
                PVP_PRO_SWORD.getIdentifier(),
                PVP_PRO_SWORD
        );
        Registry.register(Registries.ITEM,
                MINERS_PICKAXE.getIdentifier(),
                MINERS_PICKAXE
        );
        Registry.register(Registries.ITEM,
                new Identifier("battlegrounds", "flash_bang"),
                FLASH_BANG
        );
        registerDispenserBehavior();
    }

    public static void registerDispenserBehavior() {
        DispenserBlock.registerBehavior(Items.FLASH_BANG, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new FlashBangEntity(world, position.getX(), position.getY(), position.getZ(), FlashBangItem.getFuse(stack.getNbt())), (entity) ->
                        entity.setItem(stack));
            }
        });
    }

    public static void registerItemGroup() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            // 战斗用品
            content.add(PVP_PRO_SWORD.getDefaultStack());
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            // 工具与实用物品
            content.add(MINERS_PICKAXE.getDefaultStack());
            content.add(FLASH_BANG.getDefaultStack());
        });
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
                        () -> {
                            ItemStack stack = new ItemStack(Items.IRON_PICKAXE, 1);
                            stack.addEnchantment(Enchantments.SMELTING, 1);
                            return stack;
                        }
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
                PVP_PRO_SWORD, MINERS_PICKAXE
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
