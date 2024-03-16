package com.github.winexp.battlegrounds.item;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.entity.projectile.MolotovEntity;
import com.github.winexp.battlegrounds.item.weapon.*;
import com.github.winexp.battlegrounds.item.mining.MinersPickaxeItem;
import com.github.winexp.battlegrounds.item.recipe.NbtCrafting;
import com.github.winexp.battlegrounds.item.recipe.ShapedNbtCrafting;
import com.github.winexp.battlegrounds.item.recipe.ShapelessNbtCrafting;
import com.github.winexp.battlegrounds.item.thrown.FlashBangItem;
import com.github.winexp.battlegrounds.item.thrown.MolotovItem;
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
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Items extends net.minecraft.item.Items {
    public static final PVPProSwordItem PVP_PRO_SWORD = registerItem("pvp_pro_sword", new PVPProSwordItem(ToolMaterials.PVP_PRO_SWORD, 3, -2.4F, new Item.Settings().rarity(Rarity.EPIC).fireproof()));
    public static final SevenElevenSwordItem SEVEN_ELEVEN_SWORD = registerItem("seven_eleven_sword", new SevenElevenSwordItem(ToolMaterials.SEVEN_ELEVEN_SWORD, 3, -2.4F, new Item.Settings().rarity(Rarity.EPIC).fireproof()));
    public static final StevesPainSwordItem STEVES_PAIN_SWORD = registerItem("steves_pain_sword", new StevesPainSwordItem(ToolMaterials.STEVES_PAIN_SWORD, 3, -2.4F, new Item.Settings().rarity(Rarity.EPIC).fireproof()));
    public static final LeachingSwordItem LEACHING_SWORD = registerItem("leaching_sword", new LeachingSwordItem(ToolMaterials.LEACHING_SWORD, 3, -2.2F, new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final MinersPickaxeItem MINERS_PICKAXE = registerItem("miners_pickaxe", new MinersPickaxeItem(ToolMaterials.MINERS_PICKAXE, 1, -2.8F, new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final ButchersAxeItem BUTCHERS_AXE = registerItem("butchers_axe", new ButchersAxeItem(ToolMaterials.BUTCHERS_AXE, 5, -3.0F, new Item.Settings().rarity(Rarity.EPIC).fireproof()));
    public static final ChannelingBowItem CHANNELING_BOW = registerItem("channeling_bow", new ChannelingBowItem(new Item.Settings().rarity(Rarity.RARE).fireproof().maxDamage(ChannelingBowItem.DURABILITY)));
    public static final FlashBangItem FLASH_BANG = registerItem("flash_bang", new FlashBangItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));
    public static final MolotovItem MOLOTOV = registerItem("molotov", new MolotovItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));
    public static final RupertsTearItem RUPERTS_TEAR = registerItem("ruperts_tear", new RupertsTearItem(ToolMaterials.RUPERTS_TEAR, new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final KnockbackStickItem KNOCKBACK_STICK = registerItem("knockback_stick", new KnockbackStickItem(ToolMaterials.KNOCKBACK_STICK, new Item.Settings().maxCount(1).rarity(Rarity.RARE)));

    public static <T extends Item> T registerItem(String name, T item) {
        T result = Registry.register(Registries.ITEM, new Identifier("battlegrounds", name), item);
        if (item instanceof NbtCrafting nbtCrafting) {
            addRecipe(nbtCrafting);
        }
        return result;
    }

    private static void addRecipe(NbtCrafting item) {
        RecipeUtil.addRecipe(item);
    }

    public static void addCustomRecipes() {
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
                // 下界合金胸甲特供版
                new ShapedNbtCrafting(
                        new Identifier("battlegrounds", "netherite_chestplate_special"),
                        RawShapedRecipe.create(Map.of(
                                        'a', Ingredient.ofItems(NETHERITE_INGOT),
                                        'b', Ingredient.ofItems(ANCIENT_DEBRIS),
                                        'c', Ingredient.ofItems(DIAMOND_CHESTPLATE)
                                ), "a a",
                                "bcb",
                                "bbb"),
                        CraftingRecipeCategory.EQUIPMENT,
                        Util.make(NETHERITE_CHESTPLATE.getDefaultStack(), (stack) -> {
                            stack.setCustomName(Text.translatable("item.battlegrounds.netherite_chestplate_special")
                                    .styled(style -> style.withItalic(false)));
                            stack.addEnchantment(Enchantments.PROTECTION, 2);
                            stack.addEnchantment(Enchantments.VITALITY, 3);
                        })
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

        ArrayList<NbtCrafting> items = new ArrayList<>();
        items.addAll(shapedRecipes);
        items.addAll(shapelessRecipes);
        for (NbtCrafting item : items) {
            RecipeUtil.addRecipe(item.toRecipeEntry());
        }
    }

    private static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(FLASH_BANG, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new FlashBangEntity(world, position.getX(), position.getY(), position.getZ()), (entity) -> {
                    entity.setItem(stack);
                    entity.setFuse(FlashBangItem.FUSE);
                });
            }

            @Override
            protected float getForce() {
                return 1.3F;
            }
        });
        DispenserBlock.registerBehavior(MOLOTOV, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new MolotovEntity(world, position.getX(), position.getY(), position.getZ()), (entity) -> {
                    entity.setItem(stack);
                    entity.setFuse(MolotovItem.FUSE);
                });
            }

            @Override
            protected float getForce() {
                return 1.3F;
            }
        });
    }

    public static void registerModelPredicates() {
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
        registerDispenserBehaviors();
        addCustomRecipes();
    }
}
