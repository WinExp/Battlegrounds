package com.github.winexp.battlegrounds.datagen;

import com.github.winexp.battlegrounds.item.Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {
    protected ModAdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<AdvancementEntry> exporter) {
        AdvancementEntry rootAdvancement = Advancement.Builder.create()
                .display(
                        Items.BUTCHERS_AXE,
                        Text.translatable("advancements.battlegrounds.root.title"),
                        Text.translatable("advancements.battlegrounds.root.description"),
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK,
                        false, false, false
                )
                .criterion("tick", TickCriterion.Conditions.createTick())
                .build(exporter, "battlegrounds:story/root");
        AdvancementEntry wineShopAdvancement = Advancement.Builder.create()
                .parent(rootAdvancement)
                .display(
                        PotionUtil.setPotion(Items.POTION.getDefaultStack(), Potions.SWIFTNESS),
                        Text.translatable("advancements.battlegrounds.wine_shop.title"),
                        Text.translatable("advancements.battlegrounds.wine_shop.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .criterion("enter_wine_shop", TickCriterion.Conditions.createLocation(
                        Optional.of(EntityPredicate.Builder.create()
                                .location(LocationPredicate.Builder.createStructure(RegistryKey.of(
                                        RegistryKeys.STRUCTURE,
                                        new Identifier("battlegrounds", "wine_shop")
                                )))
                                .build())
                ))
                .build(exporter, "battlegrounds:story/wine_shop");
    }
}
