package com.github.winexp.battlegrounds.datagen;


import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.loot.LootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class ModChestLootTableProvider extends SimpleFabricLootTableProvider {
    public ModChestLootTableProvider(FabricDataOutput output) {
        super(output, LootContextTypes.CHEST);
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
        // metals
        exporter.accept(LootTables.METALS,
                LootTable.builder()
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(2.0F, 3.0F))
                                .with(ItemEntry.builder(Items.IRON_NUGGET).weight(1)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 32.0F))))
                                .with(ItemEntry.builder(Items.GOLD_NUGGET).weight(1)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 32.0F))))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(2.0F, 3.0F))
                                .with(ItemEntry.builder(Items.IRON_INGOT).weight(1)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
                                .with(ItemEntry.builder(Items.GOLD_INGOT).weight(1)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                                .with(ItemEntry.builder(Items.FLASH_BANG).weight(7))
                                .with(EmptyEntry.builder().weight(30))
                        )
                        .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
                                .with(ItemEntry.builder(Items.BUTCHERS_AXE).weight(1)
                                        .apply(SetDamageLootFunction.builder(new UniformLootNumberProvider(
                                                ConstantLootNumberProvider.create(0.0F),
                                                UniformLootNumberProvider.create(0.2F, 0.8F)
                                        ))))
                                .with(EmptyEntry.builder().weight(30))
                        )
        );
        // crops
        exporter.accept(LootTables.CROPS,
                LootTable.builder()
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(4.0F, 6.0F))
                                .with(ItemEntry.builder(Items.CARROT).weight(1)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
                                .with(ItemEntry.builder(Items.WHEAT).weight(1)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
                                .with(ItemEntry.builder(Items.WHEAT_SEEDS).weight(1)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
                                .with(ItemEntry.builder(Items.POTATO).weight(1)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
                        )
        );
        // wine_shop
        exporter.accept(LootTables.WINE_SHOP,
                LootTable.builder()
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(3.0F, 4.0F))
                                .with(ItemEntry.builder(Items.GLASS_BOTTLE).weight(8)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 6.0F))))
                                .with(ItemEntry.builder(Items.NETHER_WART).weight(7)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 7.0F))))
                                .with(ItemEntry.builder(Items.SOUL_SAND).weight(7)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
                                .with(ItemEntry.builder(Items.SOUL_SOIL).weight(7)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
                                .with(ItemEntry.builder(Items.GLOWSTONE_DUST).weight(5)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
                                .with(ItemEntry.builder(Items.REDSTONE).weight(5)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
                                .with(ItemEntry.builder(Items.FERMENTED_SPIDER_EYE).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
                                .with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
                                .with(ItemEntry.builder(Items.GLISTERING_MELON_SLICE).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
                                .with(ItemEntry.builder(Items.MAGMA_CREAM).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
                                .with(ItemEntry.builder(Items.PHANTOM_MEMBRANE).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
                                .with(ItemEntry.builder(Items.RABBIT_FOOT).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(2.0F, 3.0F))
                                .with(ItemEntry.builder(Items.POTION).weight(1)
                                        .apply(SetPotionLootFunction.builder(Potions.AWKWARD)))
                                .with(ItemEntry.builder(Items.POTION).weight(1)
                                        .apply(SetPotionLootFunction.builder(Potions.WATER)))
                                .with(EmptyEntry.builder().weight(2))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(2.0F, 3.0F))
                                .with(ItemEntry.builder(Items.AMETHYST_SHARD).weight(1)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 15.0F))))
                                .with(EmptyEntry.builder().weight(2))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                                .with(EmptyEntry.builder().weight(5))
                                .with(ItemEntry.builder(Items.BLAZE_POWDER).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F))))
                                .with(ItemEntry.builder(Items.BLAZE_ROD).weight(2)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                                .with(ItemEntry.builder(Items.GHAST_TEAR).weight(2)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F))))
                        )
        );
        // equipments
        exporter.accept(LootTables.EQUIPMENTS,
                LootTable.builder()
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                                .with(EmptyEntry.builder().weight(2))
                                .with(ItemEntry.builder(Items.BOW).weight(2)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.CROSSBOW).weight(2)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.ARROW).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(10.0F, 32.0F))))
                                .with(ItemEntry.builder(Items.SPECTRAL_ARROW).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8.0F, 24.0F))))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                                .with(EmptyEntry.builder().weight(3))
                                .with(ItemEntry.builder(Items.LEATHER_HELMET).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.LEATHER_CHESTPLATE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.LEATHER_LEGGINGS).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.LEATHER_BOOTS).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                                .with(EmptyEntry.builder().weight(2))
                                .with(ItemEntry.builder(Items.CHAINMAIL_HELMET).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.CHAINMAIL_CHESTPLATE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.CHAINMAIL_LEGGINGS).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.CHAINMAIL_BOOTS).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                                .with(EmptyEntry.builder().weight(3))
                                .with(ItemEntry.builder(Items.IRON_HELMET).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.IRON_CHESTPLATE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.IRON_LEGGINGS).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.IRON_BOOTS).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.IRON_SWORD).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.IRON_PICKAXE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.IRON_AXE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.IRON_SHOVEL).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                                .with(EmptyEntry.builder().weight(3))
                                .with(ItemEntry.builder(Items.GOLDEN_HELMET).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.GOLDEN_CHESTPLATE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.GOLDEN_LEGGINGS).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.GOLDEN_BOOTS).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.GOLDEN_SWORD).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.GOLDEN_PICKAXE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.GOLDEN_AXE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.GOLDEN_SHOVEL).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
                                .with(EmptyEntry.builder().weight(5))
                                .with(ItemEntry.builder(Items.DIAMOND_HELMET).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.DIAMOND_CHESTPLATE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.DIAMOND_LEGGINGS).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.DIAMOND_BOOTS).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.DIAMOND_SWORD).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.DIAMOND_PICKAXE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.DIAMOND_AXE).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                                .with(ItemEntry.builder(Items.DIAMOND_SHOVEL).weight(1)
                                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
                        )
                        .pool(LootPool.builder().rolls(UniformLootNumberProvider.create(2.0F, 3.0F))
                                .with(EmptyEntry.builder().weight(3))
                                .with(ItemEntry.builder(Items.LEATHER).weight(2)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
                                .with(ItemEntry.builder(Items.IRON_INGOT).weight(2)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 5.0F))))
                                .with(ItemEntry.builder(Items.GOLD_INGOT).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
                                .with(ItemEntry.builder(Items.DIAMOND).weight(3)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
                        )
        );
    }
}
