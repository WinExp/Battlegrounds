package com.github.winexp.battlegrounds.datagen;

import com.github.winexp.battlegrounds.component.DataComponentTypes;
import com.github.winexp.battlegrounds.datagen.recipe.ModShapedRecipeJsonBuilder;
import com.github.winexp.battlegrounds.datagen.recipe.ModShapelessRecipeJsonBuilder;
import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.concurrent.CompletableFuture;

public class ModRecipeGenerator extends FabricRecipeProvider {
    private String identifierNamespace;

    public ModRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> wrapperLookup) {
        super(output, wrapperLookup);
        this.identifierNamespace = output.getModId();
    }

    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return new Identifier(this.identifierNamespace, identifier.getPath());
    }

    @Override
    public void generate(RecipeExporter exporter) {
        this.identifierNamespace = "minecraft";
        ModShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ANVIL)
                .pattern("III")
                .pattern(" I ")
                .pattern("III")
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Items.TNT)
                .input(Items.GUNPOWDER, 2)
                .input(ItemTags.SAND);

        this.identifierNamespace = this.output.getModId();
        ModShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ADVANCED_PRECISION_CORE)
                .pattern("D D")
                .pattern(" P ")
                .pattern("DND")
                .input('D', ConventionalItemTags.DIAMOND_GEMS)
                .input('P', Items.PRECISION_CORE)
                .input('N', ConventionalItemTags.NETHERITE_INGOTS)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PRECISION_CORE)
                .pattern("GIG")
                .pattern("IQI")
                .pattern("RER")
                .input('G', ConventionalItemTags.GOLD_INGOTS)
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('Q', ConventionalItemTags.QUARTZ_GEMS)
                .input('R', ConventionalItemTags.STORAGE_BLOCKS_REDSTONE)
                .input('E', ConventionalItemTags.QUARTZ_GEMS)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BLAZE_ROD, 8)
                .input(ConventionalItemTags.WOODEN_RODS, 4)
                .input(ConventionalItemTags.LAVA_BUCKETS)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BUNDLE)
                .input(Items.LEATHER)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.COBWEB)
                .input(Items.STRING, 9)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.DIAMOND, 2)
                .pattern("ICI")
                .pattern("CIC")
                .pattern("ICI")
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('C', ItemTags.COALS)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.EXPERIENCE_BOTTLE, 4)
                .input(Items.GLASS_BOTTLE, 4)
                .input(ConventionalItemTags.LAPIS_GEMS)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.FLASH_BANG, 2)
                .pattern("RIR")
                .pattern("RSR")
                .pattern("RIR")
                .input('R', ConventionalItemTags.REDSTONE_DUSTS)
                .input('I', Items.IRON_NUGGET)
                .input('S', Items.SNOWBALL)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.FLINT)
                .input(Items.GRAVEL)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.GUNPOWDER, 8)
                .input(ItemTags.PLANKS, 4)
                .input(ItemTags.COALS)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LAVA_BUCKET)
                .input(Items.NETHERRACK, 3)
                .input(ConventionalItemTags.EMPTY_BUCKETS)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.NETHERITE_INGOT)
                .pattern("GDG")
                .pattern("DID")
                .pattern("GDG")
                .input('G', ConventionalItemTags.GOLD_INGOTS)
                .input('D', ConventionalItemTags.DIAMOND_GEMS)
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .pattern("GGG")
                .pattern("GDG")
                .pattern("GDG")
                .input('G', ConventionalItemTags.GOLD_INGOTS)
                .input('D', Items.DIAMOND)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.SADDLE)
                .pattern("LLL")
                .pattern("LIL")
                .input('L', Items.LEATHER)
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.SHULKER_BOX)
                .input(ConventionalItemTags.CHESTS, 2)
                .input(Items.BUNDLE)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.IRON_INGOT, 8)
                .input(ConventionalItemTags.IRON_RAW_MATERIALS, 8)
                .input(ItemTags.COALS)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.GOLD_INGOT, 8)
                .input(ConventionalItemTags.GOLD_RAW_MATERIALS, 8)
                .input(ItemTags.COALS)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.COPPER_INGOT, 8)
                .input(ConventionalItemTags.COPPER_RAW_MATERIALS, 8)
                .input(ItemTags.COALS)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.ARROW, 32)
                .pattern("FFF")
                .pattern("TTT")
                .pattern("SSS")
                .input('F', Items.FLINT)
                .input('T', ConventionalItemTags.WOODEN_RODS)
                .input('S', Items.STRING)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.TRIDENT)
                .pattern(" QQ")
                .pattern(" DQ")
                .pattern("D  ")
                .input('Q', ConventionalItemTags.QUARTZ_GEMS)
                .input('D', ConventionalItemTags.DIAMOND_GEMS)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.TOTEM_OF_UNDYING)
                .pattern("GGG")
                .pattern("GTG")
                .pattern("GGG")
                .input('G', ConventionalItemTags.GOLD_INGOTS)
                .input('T', Items.GHAST_TEAR)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.ZOMBIE_HEAD)
                .input(Items.DIAMOND_SWORD, 2)
                .input(Items.ROTTEN_FLESH)
                .input(Items.GOLDEN_APPLE)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.CARROT, 3)
                .input(ItemTags.DIRT)
                .input(Items.BONE_MEAL)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.GOLDEN_CARROT)
                .pattern(" G ")
                .pattern("GCG")
                .pattern(" G ")
                .input('G', Items.GOLD_NUGGET)
                .input('C', Items.CARROT)
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.APPLE)
                .input(ItemTags.LEAVES, 9)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.GOLDEN_APPLE)
                .pattern(" G ")
                .pattern("GAG")
                .pattern(" G ")
                .input('G', ConventionalItemTags.GOLD_INGOTS)
                .input('A', Items.APPLE)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.ENCHANTED_GOLDEN_APPLE)
                .pattern("LGL")
                .pattern("GAG")
                .pattern("LGL")
                .input('L', ConventionalItemTags.LAPIS_GEMS)
                .input('G', ConventionalItemTags.STORAGE_BLOCKS_GOLD)
                .input('A', Items.GOLDEN_APPLE)
                .offerTo(exporter);

        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.CHANNELING_BOW)
                .pattern("BTB")
                .pattern("AOL")
                .pattern("BGD")
                .input('B', ConventionalItemTags.STORAGE_BLOCKS_DIAMOND)
                .input('T', Items.TOTEM_OF_UNDYING)
                .input('A', Items.ANCIENT_DEBRIS)
                .input('O', Items.BOW)
                .input('L', Items.LIGHTNING_ROD)
                .input('G', Items.ENCHANTED_GOLDEN_APPLE)
                .input('D', ConventionalItemTags.DIAMOND_GEMS)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.PVP_PRO_SWORD)
                .pattern("DGD")
                .pattern("ASA")
                .pattern("BGB")
                .input('D', ConventionalItemTags.DIAMOND_GEMS)
                .input('G', ConventionalItemTags.STORAGE_BLOCKS_GOLD)
                .input('A', Items.GOLDEN_APPLE)
                .input('S', Items.DIAMOND_SWORD)
                .input('B', ConventionalItemTags.STORAGE_BLOCKS_DIAMOND)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.SEVEN_ELEVEN_SWORD)
                .pattern("DAD")
                .pattern("SNE")
                .pattern("BPB")
                .input('D', ConventionalItemTags.DIAMOND_GEMS)
                .input('A', ConventionalItemTags.AMETHYST_GEMS)
                .input('S', Items.SEVEN_ELEVEN_PRECISION_CORE)
                .input('N', Items.NETHERITE_SWORD)
                .input('E', ConventionalItemTags.EMERALD_GEMS)
                .input('B', ConventionalItemTags.STORAGE_BLOCKS_DIAMOND)
                .input('P', Items.PRECISION_CORE)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.STEVES_PAIN_SWORD)
                .pattern("DTD")
                .pattern("GLE")
                .pattern("DPD")
                .input('D', ConventionalItemTags.STORAGE_BLOCKS_DIAMOND)
                .input('T', Items.GHAST_TEAR)
                .input('G', Items.GOLDEN_APPLE)
                .input('L', Items.LEACHING_SWORD)
                .input('E', Items.ENCHANTED_GOLDEN_APPLE)
                .input('P', Items.ADVANCED_PRECISION_CORE)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.MY_HOLY_SWORD)
                .pattern("DPD")
                .pattern("ENE")
                .pattern("DPD")
                .input('D', ConventionalItemTags.STORAGE_BLOCKS_DIAMOND)
                .input('P', Items.ADVANCED_PRECISION_CORE)
                .input('E', Items.ENCHANTED_GOLDEN_APPLE)
                .input('N', Items.NETHERITE_SWORD)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.LEACHING_SWORD)
                .pattern("DCB")
                .pattern("ESE")
                .pattern("DCD")
                .input('D', ConventionalItemTags.DIAMOND_GEMS)
                .input('C', Items.COBWEB)
                .input('B', ConventionalItemTags.STORAGE_BLOCKS_DIAMOND)
                .input('E', Items.SPIDER_EYE)
                .input('S', Items.DIAMOND_SWORD)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.MINERS_PICKAXE)
                .pattern("RRR")
                .pattern("CSC")
                .pattern(" S ")
                .input('R', ConventionalItemTags.IRON_RAW_MATERIALS)
                .input('C', ItemTags.COALS)
                .input('S', ConventionalItemTags.WOODEN_RODS)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.KNOCKBACK_STICK)
                .pattern("DBD")
                .pattern("BSB")
                .pattern("DBD")
                .input('D', ConventionalItemTags.DIAMOND_GEMS)
                .input('B', ConventionalItemTags.BLAZE_RODS)
                .input('S', ConventionalItemTags.WOODEN_RODS)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.NETHERITE_SWORD)
                .pattern("PGP")
                .pattern("GSG")
                .pattern("PGP")
                .input('P', Items.GUNPOWDER)
                .input('G', ConventionalItemTags.GOLD_INGOTS)
                .input('S', Items.DIAMOND_SWORD)
                .offerTo(exporter);
        ModShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ENCHANTED_BOOK)
                .pattern("FPF")
                .pattern("PIP")
                .pattern("FPF")
                .input('F', ConventionalItemTags.PLAYER_WORKSTATIONS_FURNACES)
                .input('P', Items.STONE_PICKAXE)
                .input('I', ConventionalItemTags.IRON_RAW_MATERIALS)
                .component(DataComponentTypes.STORED_ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                            builder.add(Enchantments.SMELTING, 1);
                        })
                        .build())
                .offerTo(exporter, new Identifier("battlegrounds", "enchanted_book_smelting_1"));
        ModShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.NETHERITE_CHESTPLATE)
                .pattern("N N")
                .pattern("DCD")
                .pattern("DDD")
                .input('N', ConventionalItemTags.NETHERITE_INGOTS)
                .input('D', Items.ANCIENT_DEBRIS)
                .input('C', Items.DIAMOND_CHESTPLATE)
                .component(DataComponentTypes.ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                            builder.add(Enchantments.PROTECTION, 3);
                            builder.add(Enchantments.VITALITY, 3);
                            builder.add(Enchantments.THORNS, 3);
                        })
                        .build())
                .component(DataComponentTypes.ITEM_NAME, Text.translatable("item.battlegrounds.netherite_chestplate_special"))
                .offerTo(exporter, new Identifier("battlegrounds", "netherite_chestplate_special"));
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ENCHANTED_BOOK)
                .input(Items.IRON_SWORD)
                .input(Items.BOOK)
                .component(DataComponentTypes.STORED_ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                            builder.add(Enchantments.SHARPNESS, 2);
                        })
                        .build())
                .offerTo(exporter);
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ENCHANTED_BOOK)
                .input(Items.IRON_CHESTPLATE)
                .input(Items.BOOK)
                .component(DataComponentTypes.STORED_ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                            builder.add(Enchantments.PROTECTION, 1);
                        })
                        .build())
                .offerTo(exporter, new Identifier("battlegrounds", "enchanted_book_protection_1"));
        ModShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ENCHANTED_BOOK)
                .input(Items.BOW)
                .input(Items.BOOK)
                .component(DataComponentTypes.STORED_ENCHANTMENTS, Util.make(new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder -> {
                            builder.add(Enchantments.POWER, 2);
                        })
                        .build())
                .offerTo(exporter, new Identifier("battlegrounds", "enchanted_book_power_2"));
    }
}
