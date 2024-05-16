package com.github.winexp.battlegrounds.item;

import com.github.winexp.battlegrounds.block.Blocks;
import com.github.winexp.battlegrounds.component.DataComponentTypes;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Items extends net.minecraft.item.Items {
    private static final List<NbtRecipe> NBT_RECIPE_ITEMS = new ArrayList<>();
    public static final PVPProSwordItem PVP_PRO_SWORD = registerItem("pvp_pro_sword", new PVPProSwordItem(ToolMaterials.PVP_PRO_SWORD, new LegendarySwordItem.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .attributeModifiers(MyHolySwordItem.createAttributeModifiers(ToolMaterials.PVP_PRO_SWORD, 3, -2.4F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                builder.add(Enchantments.FIRE_ASPECT, 2);
                builder.add(Enchantments.KNOCKBACK, 2);
                builder.add(Enchantments.SWEEPING_EDGE, 2);
                builder.add(Enchantments.LOOTING, 3);
            }).build())
            .enrichEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2 * 20, 0))
            .enrichEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 2 * 20, 0))
            .enrichEffect(new StatusEffectInstance(StatusEffects.SPEED, 2 * 20, 0))
            .glint()
    ));
    public static final SevenElevenSwordItem SEVEN_ELEVEN_SWORD = registerItem("seven_eleven_sword", new SevenElevenSwordItem(ToolMaterials.SEVEN_ELEVEN_SWORD, new LegendarySwordItem.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .glint()
            .attributeModifiers(MyHolySwordItem.createAttributeModifiers(ToolMaterials.SEVEN_ELEVEN_SWORD, 3, -2.4F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                builder.add(Enchantments.FIRE_ASPECT, 2);
                builder.add(Enchantments.KNOCKBACK, 2);
                builder.add(Enchantments.SWEEPING_EDGE, 3);
                builder.add(Enchantments.LOOTING, 3);
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
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                builder.add(Enchantments.STEVES_PAIN, 1);
                builder.add(Enchantments.FIRE_ASPECT, 2);
                builder.add(Enchantments.MENDING, 1);
            }).build())
    ));
    public static final MyHolySwordItem MY_HOLY_SWORD = registerItem("my_holy_sword", new MyHolySwordItem(ToolMaterials.MY_HOLY_SWORD, new LegendarySwordItem.Settings()
            .rarity(Rarity.EPIC)
            .fireproof()
            .glint()
            .attributeModifiers(MyHolySwordItem.createAttributeModifiers(ToolMaterials.MY_HOLY_SWORD, 3, -2.4F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                builder.add(Enchantments.SHARPNESS, 5);
                builder.add(Enchantments.FIRE_ASPECT, 2);
                builder.add(Enchantments.KNOCKBACK, 2);
                builder.add(Enchantments.SWEEPING_EDGE, 2);
            }).build())
            .enrichEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2 * 20, 0))
            .attackEffect(new StatusEffectInstance(StatusEffects.WITHER, 6 * 20, 1))
    ));
    public static final LeachingSwordItem LEACHING_SWORD = registerItem("leaching_sword", new LeachingSwordItem(ToolMaterials.LEACHING_SWORD, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
            .attributeModifiers(MyHolySwordItem.createAttributeModifiers(ToolMaterials.LEACHING_SWORD, 3, -2.0F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                builder.add(Enchantments.LEACHING, 1);
                builder.add(Enchantments.KNOCKBACK, 3);
                builder.add(Enchantments.FIRE_ASPECT, 3);
            }).build())
    ));
    public static final MinersPickaxeItem MINERS_PICKAXE = registerItem("miners_pickaxe", new MinersPickaxeItem(ToolMaterials.MINERS_PICKAXE, new Item.Settings()
            .rarity(Rarity.UNCOMMON)
            .attributeModifiers(PickaxeItem.createAttributeModifiers(ToolMaterials.MINERS_PICKAXE, 1, -2.8F))
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                builder.add(Enchantments.FORTUNE, 2);
                builder.add(Enchantments.EFFICIENCY, 4);
                builder.add(Enchantments.SMELTING, 1);
                builder.add(Enchantments.MENDING, 1);
                builder.add(Enchantments.UNBREAKING, 3);
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
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                builder.add(Enchantments.CHANNELING_PRO, 1);
                builder.add(Enchantments.POWER, 5);
                builder.add(Enchantments.PUNCH, 3);
                builder.add(Enchantments.FLAME, 1);
                builder.add(Enchantments.FIRE_ASPECT, 2);
                builder.add(Enchantments.SHARPNESS, 9);
                builder.add(Enchantments.INFINITY, 1);
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
            .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                builder.add(Enchantments.KNOCKBACK, 7);
                builder.add(Enchantments.SHARPNESS, 1);
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
    public static final BlockItem SOAK_TABLE = registerBlockItem("soak_table", Blocks.SOAK_TABLE);

    private static BlockItem registerBlockItem(String id, Block block) {
        return registerItem(id, new BlockItem(block, new Item.Settings()));
    }

    private static <T extends Item> T registerItem(String id, T item) {
        register(new Identifier("battlegrounds", id), item);
        if (item instanceof NbtRecipe nbtRecipe) {
            NBT_RECIPE_ITEMS.add(nbtRecipe);
        }
        return item;
    }

    public static void addRecipes() {
        for (NbtRecipe nbtRecipe : NBT_RECIPE_ITEMS) {
            RecipeUtil.addRecipe(nbtRecipe);
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
