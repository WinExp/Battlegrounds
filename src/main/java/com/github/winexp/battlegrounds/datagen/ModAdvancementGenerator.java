package com.github.winexp.battlegrounds.datagen;

import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.world.gen.structure.Structures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementGenerator extends FabricAdvancementProvider {
    protected ModAdvancementGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> wrapperLookup) {
        super(output, wrapperLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup wrapperLookup, Consumer<AdvancementEntry> exporter) {
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
        this.generateItemAdvancements(wrapperLookup ,rootAdvancement, exporter);
        this.generateStructureAdvancements(wrapperLookup, rootAdvancement, exporter);
    }

    @SafeVarargs
    private Advancement.Builder requiredAllStructuresArrived(RegistryWrapper.WrapperLookup wrapperLookup, Advancement.Builder builder, RegistryKey<Structure>... structures) {
        return this.requiredAllStructuresArrived(wrapperLookup, builder, Arrays.stream(structures).toList());
    }

    private Advancement.Builder requiredAllStructuresArrived(RegistryWrapper.WrapperLookup wrapperLookup, Advancement.Builder builder, Collection<RegistryKey<Structure>> structures) {
        for (RegistryKey<Structure> structure : structures) {
            builder.criterion(structure.getValue().getPath(), TickCriterion.Conditions.createLocation(
                    LocationPredicate.Builder.createStructure(wrapperLookup.getWrapperOrThrow(RegistryKeys.STRUCTURE)
                            .getOrThrow(structure))
            ));
        }
        return builder;
    }

    public void generateStructureAdvancements(RegistryWrapper.WrapperLookup wrapperLookup, AdvancementEntry rootAdvancement, Consumer<AdvancementEntry> exporter) {
        AdvancementEntry medievalFortressAdvancement = this.requiredAllStructuresArrived(
                        wrapperLookup,
                        Advancement.Builder.create(),
                        Structures.MEDIEVAL_FORTRESS
                )
                .parent(rootAdvancement)
                .display(
                        Items.STONE_BRICKS,
                        Text.translatable("advancements.battlegrounds.medieval_fortress.title"),
                        Text.translatable("advancements.battlegrounds.medieval_fortress.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "battlegrounds:story/medieval_fortress");
        AdvancementEntry relicOfFantasyAdvancement = this.requiredAllStructuresArrived(
                        wrapperLookup,
                        Advancement.Builder.create(),
                        Structures.RELIC_OF_FANTASY
                )
                .parent(rootAdvancement)
                .display(
                        Items.SMOOTH_QUARTZ,
                        Text.translatable("advancements.battlegrounds.relic_of_fantasy.title"),
                        Text.translatable("advancements.battlegrounds.relic_of_fantasy.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "battlegrounds:story/relic_of_fantasy");
        AdvancementEntry wineShopAdvancement = this.requiredAllStructuresArrived(
                        wrapperLookup,
                        Advancement.Builder.create(),
                        Structures.WINE_SHOP
                )
                .parent(rootAdvancement)
                .display(
                        PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.STRONG_SWIFTNESS),
                        Text.translatable("advancements.battlegrounds.wine_shop.title"),
                        Text.translatable("advancements.battlegrounds.wine_shop.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "battlegrounds:story/wine_shop");
        AdvancementEntry libraryAdvancement = this.requiredAllStructuresArrived(
                        wrapperLookup,
                        Advancement.Builder.create(),
                        Structures.LIBRARY
                )
                .parent(rootAdvancement)
                .display(
                        Items.BOOKSHELF,
                        Text.translatable("advancements.battlegrounds.library.title"),
                        Text.translatable("advancements.battlegrounds.library.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "battlegrounds:story/library");
        AdvancementEntry allStructuresAdvancement = this.requiredAllStructuresArrived(
                        wrapperLookup,
                        Advancement.Builder.create(),
                        Structures.ANCIENT_RUINS,
                        Structures.CANOPIES,
                        Structures.DESERT_HUT,
                        Structures.IZAKAYA,
                        Structures.KIOSK,
                        Structures.LIBRARY,
                        Structures.MEDIEVAL_FORTRESS,
                        Structures.MEDIEVAL_LIBRARY,
                        Structures.WINE_SHOP
                )
                .parent(rootAdvancement)
                .display(
                        Items.DIAMOND,
                        Text.translatable("advancements.battlegrounds.all_structures.title"),
                        Text.translatable("advancements.battlegrounds.all_structures.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .rewards(AdvancementRewards.Builder.experience(1000))
                .build(exporter, "battlegrounds:story/arrived_all_structures");
    }

    public void generateItemAdvancements(RegistryWrapper.WrapperLookup wrapperLookup, AdvancementEntry rootAdvancement, Consumer<AdvancementEntry> exporter) {
        AdvancementEntry pvpProAdvancement = Advancement.Builder.create()
                .parent(rootAdvancement)
                .display(
                        Items.PVP_PRO_SWORD,
                        Text.translatable("advancements.battlegrounds.pvp_pro.title"),
                        Text.translatable("advancements.battlegrounds.pvp_pro.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("pvp_pro_sword", InventoryChangedCriterion.Conditions.items(
                        Items.PVP_PRO_SWORD
                ))
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "battlegrounds:story/pvp_pro");
        AdvancementEntry sevenElevenAdvancement = Advancement.Builder.create()
                .parent(rootAdvancement)
                .display(
                        Items.SEVEN_ELEVEN_SWORD,
                        Text.translatable("advancements.battlegrounds.seven_eleven.title"),
                        Text.translatable("advancements.battlegrounds.seven_eleven.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true, true, false
                )
                .criterion("seven_eleven_sword", InventoryChangedCriterion.Conditions.items(
                        Items.SEVEN_ELEVEN_SWORD
                ))
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "battlegrounds:story/seven_eleven");
        AdvancementEntry stevesPainAdvancement = Advancement.Builder.create()
                .parent(rootAdvancement)
                .display(
                        Items.STEVES_PAIN_SWORD,
                        Text.translatable("advancements.battlegrounds.steves_pain.title"),
                        Text.translatable("advancements.battlegrounds.steves_pain.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("seven_eleven_sword", InventoryChangedCriterion.Conditions.items(
                        Items.STEVES_PAIN_SWORD
                ))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(exporter, "battlegrounds:story/steves_pain");
        AdvancementEntry channelingBowAdvancement = Advancement.Builder.create()
                .parent(rootAdvancement)
                .display(
                        Items.CHANNELING_BOW,
                        Text.translatable("advancements.battlegrounds.channeling_bow.title"),
                        Text.translatable("advancements.battlegrounds.channeling_bow.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("channeling_bow", InventoryChangedCriterion.Conditions.items(
                        Items.CHANNELING_BOW
                ))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(exporter, "battlegrounds:story/channeling_bow");
        AdvancementEntry butchersAxeAdvancement = Advancement.Builder.create()
                .parent(rootAdvancement)
                .display(
                        Items.BUTCHERS_AXE,
                        Text.translatable("advancements.battlegrounds.butchers_axe.title"),
                        Text.translatable("advancements.battlegrounds.butchers_axe.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true, true, false
                )
                .criterion("butchers_axe", InventoryChangedCriterion.Conditions.items(
                        Items.BUTCHERS_AXE
                ))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(exporter, "battlegrounds:story/butchers_axe");
    }
}
