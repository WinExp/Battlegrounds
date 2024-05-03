package com.github.winexp.battlegrounds.item;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.item.food.*;
import com.github.winexp.battlegrounds.item.ingredient.*;
import com.github.winexp.battlegrounds.item.recipe.*;
import com.github.winexp.battlegrounds.item.tool.ToolMaterials;
import com.github.winexp.battlegrounds.item.weapon.*;
import com.github.winexp.battlegrounds.item.mining.*;
import com.github.winexp.battlegrounds.item.thrown.*;
import com.github.winexp.battlegrounds.item.tool.*;
import com.github.winexp.battlegrounds.util.RecipeUtil;
import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Items extends net.minecraft.item.Items {
    private static final List<NbtRecipe> NBT_RECIPE_ITEMS = new ArrayList<>();
    public static final PVPProSwordItem PVP_PRO_SWORD = registerItem("pvp_pro_sword", new PVPProSwordItem(ToolMaterials.PVP_PRO_SWORD, new LegendarySwordItem.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .attributeModifiers(MyHolySwordItem.createAttributeModifiers(ToolMaterials.PVP_PRO_SWORD, 3, -2.4F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), component -> {
                component.add(Enchantments.FIRE_ASPECT, 2);
                component.add(Enchantments.KNOCKBACK, 2);
                component.add(Enchantments.SWEEPING_EDGE, 2);
                component.add(Enchantments.LOOTING, 3);
            }).build())
            .enrichEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 2 * 20, 0))
            .enrichEffect(new StatusEffectInstance(StatusEffects.SPEED, 2 * 20, 0))
            .glint()
    ));
    public static final SevenElevenSwordItem SEVEN_ELEVEN_SWORD = registerItem("seven_eleven_sword", new SevenElevenSwordItem(ToolMaterials.SEVEN_ELEVEN_SWORD, new LegendarySwordItem.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .glint()
            .attributeModifiers(MyHolySwordItem.createAttributeModifiers(ToolMaterials.SEVEN_ELEVEN_SWORD, 3, -2.4F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), component -> {
                component.add(Enchantments.FIRE_ASPECT, 2);
                component.add(Enchantments.KNOCKBACK, 2);
                component.add(Enchantments.SWEEPING_EDGE, 3);
                component.add(Enchantments.LOOTING, 3);
            }).build())
            .enrichEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 2 * 20, 1))
            .enrichEffect(new StatusEffectInstance(StatusEffects.SPEED, 2 * 20, 1))
            .attackEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * 20, 1))
            .attackEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 5 * 20, 0))
            .attackEffectBound(5)
    ));
    public static final StevesPainSwordItem STEVES_PAIN_SWORD = registerItem("steves_pain_sword", new StevesPainSwordItem(ToolMaterials.STEVES_PAIN_SWORD, new LegendarySwordItem.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .glint()
            .attributeModifiers(MyHolySwordItem.createAttributeModifiers(ToolMaterials.STEVES_PAIN_SWORD, 3, -2.4F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), component -> {
                component.add(Enchantments.STEVES_PAIN, 1);
                component.add(Enchantments.FIRE_ASPECT, 2);
                component.add(Enchantments.MENDING, 1);
            }).build())
    ));
    public static final MyHolySwordItem MY_HOLY_SWORD = registerItem("my_holy_sword", new MyHolySwordItem(ToolMaterials.MY_HOLY_SWORD, new LegendarySwordItem.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .glint()
            .attributeModifiers(MyHolySwordItem.createAttributeModifiers(ToolMaterials.MY_HOLY_SWORD, 3, -2.4F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), component -> {
                component.add(Enchantments.SHARPNESS, 5);
                component.add(Enchantments.FIRE_ASPECT, 2);
                component.add(Enchantments.KNOCKBACK, 2);
                component.add(Enchantments.SWEEPING_EDGE, 2);
            }).build())
            .enrichEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2 * 20, 0))
            .attackEffect(new StatusEffectInstance(StatusEffects.WITHER, 6 * 20, 1))
    ));
    public static final LeachingSwordItem LEACHING_SWORD = registerItem("leaching_sword", new LeachingSwordItem(ToolMaterials.LEACHING_SWORD, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
            .attributeModifiers(MyHolySwordItem.createAttributeModifiers(ToolMaterials.LEACHING_SWORD, 3, -2.0F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), component -> {
                component.add(Enchantments.LEACHING, 1);
                component.add(Enchantments.KNOCKBACK, 3);
                component.add(Enchantments.FIRE_ASPECT, 3);
            }).build())
    ));
    public static final MinersPickaxeItem MINERS_PICKAXE = registerItem("miners_pickaxe", new MinersPickaxeItem(ToolMaterials.MINERS_PICKAXE, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
            .attributeModifiers(PickaxeItem.createAttributeModifiers(ToolMaterials.MINERS_PICKAXE, 1, -2.8F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), component -> {
                component.add(Enchantments.FORTUNE, 1);
                component.add(Enchantments.EFFICIENCY, 3);
                component.add(Enchantments.SMELTING, 1);
                component.add(Enchantments.MENDING, 1);
                component.add(Enchantments.UNBREAKING, 3);
            }).build())
    ));
    public static final ButchersAxeItem BUTCHERS_AXE = registerItem("butchers_axe", new ButchersAxeItem(ToolMaterials.BUTCHERS_AXE, new Item.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .attributeModifiers(AxeItem.createAttributeModifiers(ToolMaterials.BUTCHERS_AXE, 5, -3.3F))
    ));
    public static final BowItem CHANNELING_BOW = registerItem("channeling_bow", new BowItem(new Item.Settings()
            .rarity(Rarity.RARE)
            .fireproof()
            .maxDamage(500)
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), component -> {
                component.add(Enchantments.CHANNELING_PRO, 1);
                component.add(Enchantments.POWER, 5);
                component.add(Enchantments.PUNCH, 3);
                component.add(Enchantments.FLAME, 1);
                component.add(Enchantments.FIRE_ASPECT, 2);
                component.add(Enchantments.SHARPNESS, 9);
                component.add(Enchantments.INFINITY, 1);
            }).build())
    ));

    public static final FlashBangItem FLASH_BANG = registerItem("flash_bang", new FlashBangItem(new Item.Settings()
            .maxCount(16)
            .rarity(Rarity.UNCOMMON)
    ));
    public static final MolotovItem MOLOTOV = registerItem("molotov", new MolotovItem(new Item.Settings()
            .maxCount(16)
            .rarity(Rarity.UNCOMMON)
    ));
    public static final RupertsTearItem RUPERTS_TEAR = registerItem("ruperts_tear", new RupertsTearItem(ToolMaterials.RUPERTS_TEAR, new Item.Settings()
            .maxCount(1)
            .rarity(Rarity.RARE)
    ));
    public static final KnockbackStickItem KNOCKBACK_STICK = registerItem("knockback_stick", new KnockbackStickItem(ToolMaterials.KNOCKBACK_STICK, new Item.Settings()
            .maxCount(1)
            .rarity(Rarity.RARE)
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), component -> {
                component.add(Enchantments.KNOCKBACK, 7);
                component.add(Enchantments.SHARPNESS, 1);
            }).build())
    ));

    public static final Item PRECISION_CORE = registerItem("precision_core", new Item(new Item.Settings()
            .rarity(Rarity.COMMON)
            .maxCount(16)
    ));
    public static final AdvancedPrecisionCoreItem ADVANCED_PRECISION_CORE = registerItem("advanced_precision_core", new AdvancedPrecisionCoreItem(new Item.Settings()
            .rarity(Rarity.UNCOMMON)
            .maxCount(8)
    ));
    public static final AdvancedPrecisionCoreItem SEVEN_ELEVEN_PRECISION_CORE = registerItem("seven_eleven_precision_core", new AdvancedPrecisionCoreItem(new Item.Settings()
            .rarity(Rarity.EPIC)
            .maxCount(8)
    ));

    public static final Item BEEF_NOODLE_SOUP = registerItem("beef_noodle_soup", new Item(new Item.Settings()
            .rarity(Rarity.UNCOMMON)
            .maxCount(1)
            .food(new FoodComponent.Builder()
                    .alwaysEdible()
                    .nutrition(8)
                    .saturationModifier(0.75F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 30 * 20, 0), 1.0F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 30 * 20, 0), 1.0F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.ADRENALINE, 10 * 20, 0), 1.0F)
                    .build()
            )
    ));
    public static final Item SIX_FLAVOURED_DIHUANG_PILL = registerItem("six_flavoured_dihuang_pill", new SixFlavouredDihuangPillItem(new Item.Settings()
            .rarity(Rarity.UNCOMMON)
            .maxCount(16)
            .food(new FoodComponent.Builder()
                    .alwaysEdible()
                    .snack()
                    .nutrition(4)
                    .saturationModifier(0.75F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 8 * 20, 2), 1.0F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 8 * 20, 1), 1.0F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 8 * 20, 1), 1.0F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 8 * 20, 2), 1.0F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.ADRENALINE, 8 * 20, 0), 1.0F)
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
        List<ShapedNbtRecipe> shapedRecipes = ImmutableList.of(
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
                        Util.make(new ItemStack(NETHERITE_CHESTPLATE), (stack) -> {
                            stack.applyChanges(ComponentChanges.builder()
                                    .add(DataComponentTypes.ITEM_NAME, Text.translatable("item.battlegrounds.netherite_chestplate_special")
                                            .styled(style -> style.withItalic(false)))
                                    .build()
                            );
                            stack.addEnchantment(Enchantments.PROTECTION, 3);
                            stack.addEnchantment(Enchantments.VITALITY, 3);
                            stack.addEnchantment(Enchantments.THORNS, 3);
                        })
                )
        );
        List<ShapelessNbtRecipe> shapelessRecipes = ImmutableList.of(
                // 锋利 1 附魔书
                new ShapelessNbtRecipe(
                        new Identifier("battlegrounds", "enchanted_book_sharpness_1"),
                        CraftingRecipeCategory.EQUIPMENT,
                        EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(
                                Enchantments.SHARPNESS, 1
                        )),
                        ImmutableList.of(
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
                        ImmutableList.of(
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
                        ImmutableList.of(
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
        DispenserBlock.registerBehavior(FLASH_BANG, new ProjectileDispenserBehavior(Items.FLASH_BANG));
        DispenserBlock.registerBehavior(MOLOTOV, new ProjectileDispenserBehavior(Items.MOLOTOV));
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

    public static void bootstrap() {
        registerDispenserBehaviors();
        addRecipes();
    }
}
