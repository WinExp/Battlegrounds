package com.github.winexp.battlegrounds.item;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.entity.projectile.thrown.FlashBangEntity;
import com.github.winexp.battlegrounds.entity.projectile.thrown.MolotovEntity;
import com.github.winexp.battlegrounds.item.recipe.*;
import com.github.winexp.battlegrounds.item.weapon.*;
import com.github.winexp.battlegrounds.item.mining.MinersPickaxeItem;
import com.github.winexp.battlegrounds.item.thrown.FlashBangItem;
import com.github.winexp.battlegrounds.item.thrown.MolotovItem;
import com.github.winexp.battlegrounds.item.tool.*;
import com.github.winexp.battlegrounds.util.RecipeUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Items extends net.minecraft.item.Items {
    private static final List<NbtRecipe> NBT_RECIPE_ITEMS = new ArrayList<>();
    public static final LegendarySwordItem PVP_PRO_SWORD = registerItem("pvp_pro_sword", new LegendarySwordItem(ToolMaterials.PVP_PRO_SWORD, 3, -2.4F, new LegendarySwordItem.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .enrichEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 0, 2 * 20))
            .glint()
            .recipe(() -> new ShapedItemNbtRecipe(
                    RawShapedRecipe.create(
                            Map.of(
                                    'a', Ingredient.ofItems(Items.DIAMOND),
                                    'b', Ingredient.ofItems(Items.GOLD_BLOCK),
                                    'c', Ingredient.ofItems(Items.TOTEM_OF_UNDYING, Items.GOLDEN_APPLE),
                                    'd', Ingredient.ofItems(Items.DIAMOND_SWORD),
                                    'e', Ingredient.ofItems(Items.DIAMOND_BLOCK)
                            ),
                            "aba",
                            "cdc",
                            "ebe"
                    ),
                    CraftingRecipeCategory.EQUIPMENT,
                    Util.make(() -> {
                        ItemStack stack = new ItemStack(Items.PVP_PRO_SWORD);
                        stack.addEnchantment(Enchantments.FIRE_ASPECT, 2);
                        stack.addEnchantment(Enchantments.KNOCKBACK, 3);
                        stack.addEnchantment(Enchantments.SWEEPING, 2);
                        stack.addEnchantment(Enchantments.LOOTING, 3);
                        return stack;
                    })
            ))
            .tooltip(Text.translatable("item.battlegrounds.pvp_pro_sword.tooltip.1")
                    .formatted(Formatting.GOLD)
                    .styled(style -> style.withBold(true)))
            .tooltip(Text.translatable("item.battlegrounds.pvp_pro_sword.tooltip.2")
                    .formatted(Formatting.GOLD)
                    .styled(style -> style.withBold(true)))
            .tooltip(Text.translatable("item.battlegrounds.pvp_pro_sword.tooltip.3")
                    .formatted(Formatting.GOLD)
                    .styled(style -> style.withBold(true)))
    ));
    public static final LegendarySwordItem SEVEN_ELEVEN_SWORD = registerItem("seven_eleven_sword", new LegendarySwordItem(ToolMaterials.SEVEN_ELEVEN_SWORD, 3, -2.4F, new LegendarySwordItem.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .glint()
            .enrichEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 1, 2 * 20))
            .enrichEffect(new StatusEffectInstance(StatusEffects.SPEED, 1, 2 * 20))
            .attackEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1, 5 * 20))
            .attackEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 0, 5 * 20))
            .attackEffectBound(35)
            .recipe(() -> new ShapedItemNbtRecipe(
                    RawShapedRecipe.create(
                            Map.of(
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
                            "fgf"
                    ),
                    CraftingRecipeCategory.EQUIPMENT,
                    Util.make(() -> {
                        ItemStack stack = new ItemStack(Items.SEVEN_ELEVEN_SWORD);
                        stack.addEnchantment(Enchantments.FIRE_ASPECT, 2);
                        stack.addEnchantment(Enchantments.KNOCKBACK, 2);
                        stack.addEnchantment(Enchantments.SWEEPING, 3);
                        stack.addEnchantment(Enchantments.LOOTING, 3);
                        return stack;
                    })
            ))
            .tooltip(Text.translatable("item.battlegrounds.seven_eleven_sword.tooltip.1")
                    .formatted(Formatting.GOLD)
                    .styled(style -> style.withBold(true)))
            .tooltip(Text.translatable("item.battlegrounds.seven_eleven_sword.tooltip.2")
                    .formatted(Formatting.GOLD)
                    .styled(style -> style.withBold(true)))
    ));
    public static final StevesPainSwordItem STEVES_PAIN_SWORD = registerItem("steves_pain_sword", new StevesPainSwordItem(ToolMaterials.STEVES_PAIN_SWORD, 3, -2.4F, new LegendarySwordItem.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .glint()
    ));
    public static final LeachingSwordItem LEACHING_SWORD = registerItem("leaching_sword", new LeachingSwordItem(ToolMaterials.LEACHING_SWORD, 3, -2.0F, new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final MinersPickaxeItem MINERS_PICKAXE = registerItem("miners_pickaxe", new MinersPickaxeItem(ToolMaterials.MINERS_PICKAXE, 1, -2.8F, new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final ButchersAxeItem BUTCHERS_AXE = registerItem("butchers_axe", new ButchersAxeItem(ToolMaterials.BUTCHERS_AXE, 5, -3.3F, new FabricItemSettings().rarity(Rarity.EPIC).fireproof()));
    public static final ChannelingBowItem CHANNELING_BOW = registerItem("channeling_bow", new ChannelingBowItem(new FabricItemSettings().rarity(Rarity.RARE).fireproof().maxDamage(ChannelingBowItem.DURABILITY)));

    public static final FlashBangItem FLASH_BANG = registerItem("flash_bang", new FlashBangItem(new FabricItemSettings().maxCount(16).rarity(Rarity.UNCOMMON)));
    public static final MolotovItem MOLOTOV = registerItem("molotov", new MolotovItem(new FabricItemSettings().maxCount(16).rarity(Rarity.UNCOMMON)));
    public static final RupertsTearItem RUPERTS_TEAR = registerItem("ruperts_tear", new RupertsTearItem(ToolMaterials.RUPERTS_TEAR, new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
    public static final KnockbackStickItem KNOCKBACK_STICK = registerItem("knockback_stick", new KnockbackStickItem(ToolMaterials.KNOCKBACK_STICK, new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item BEEF_NOODLE_SOUP = registerItem("beef_noodle_soup", new Item(new FabricItemSettings()
            .rarity(Rarity.UNCOMMON)
            .food(new FoodComponent.Builder()
                    .alwaysEdible()
                    .meat()
                    .hunger(6)
                    .saturationModifier(0.7F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 15 * 20, 0), 1.0F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 15 * 20, 0), 1.0F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.ADRENALINE, 15 * 20, 0), 1.0F)
                    .build()
            )
    ));

    public static <T extends Item> T registerItem(String name, T item) {
        T result = Registry.register(Registries.ITEM, new Identifier("battlegrounds", name), item);
        if (item instanceof NbtRecipe nbtRecipe) {
            NBT_RECIPE_ITEMS.add(nbtRecipe);
        }
        return result;
    }

    public static void addRecipes() {
        for (NbtRecipe nbtRecipe : NBT_RECIPE_ITEMS) {
            RecipeUtil.addRecipe(nbtRecipe);
        }
        addCustomRecipes();
    }

    private static void addCustomRecipes() {
        List<ShapedNbtRecipe> shapedRecipes = List.of(
                // 自动冶炼
                new ShapedNbtRecipe(
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
                new ShapedNbtRecipe(
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
                            stack.addEnchantment(Enchantments.PROTECTION, 3);
                            stack.addEnchantment(Enchantments.VITALITY, 3);
                            stack.addEnchantment(Enchantments.THORNS, 3);
                        })
                )
        );
        List<ShapelessNbtRecipe> shapelessRecipes = List.of(
                // 锋利 1 附魔书
                new ShapelessNbtRecipe(
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
                new ShapelessNbtRecipe(
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
                new ShapelessNbtRecipe(
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
        List<NbtRecipe> items = new ArrayList<>();
        items.addAll(shapedRecipes);
        items.addAll(shapelessRecipes);
        for (NbtRecipe item : items) {
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
        });
        DispenserBlock.registerBehavior(MOLOTOV, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new MolotovEntity(world, position.getX(), position.getY(), position.getZ()), (entity) -> {
                    entity.setItem(stack);
                    entity.setFuse(MolotovItem.FUSE);
                });
            }
        });
    }

    @Environment(EnvType.CLIENT)
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
        addRecipes();
    }
}
