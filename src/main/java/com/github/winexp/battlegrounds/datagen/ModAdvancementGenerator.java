package com.github.winexp.battlegrounds.datagen;

import com.github.winexp.battlegrounds.item.Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class ModAdvancementGenerator extends FabricAdvancementProvider {
    protected ModAdvancementGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<AdvancementEntry> exporter) {
        AdvancementEntry rootAdvancement = Advancement.Builder.create()
                .display(
                        Items.BUTCHERS_AXE.getDefaultStack(),
                        Text.translatable("advancements.battlegrounds.root.title"),
                        Text.translatable("advancements.battlegrounds.root.description"),
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK,
                        false, false, false
                )
                .criterion("tick", TickCriterion.Conditions.createTick())
                .build(exporter, "battlegrounds:story/root");
        this.generateItemAdvancements(rootAdvancement, exporter);
        this.generateStructureAdvancements(rootAdvancement, exporter);
    }

    @SafeVarargs
    private Advancement.Builder requiredAllStructuresArrived(Advancement.Builder builder, RegistryKey<Structure>... structures) {
        return this.requiredAllStructuresArrived(builder, Arrays.stream(structures).toList());
    }

    private Advancement.Builder requiredAllStructuresArrived(Advancement.Builder builder, Collection<RegistryKey<Structure>> structures) {
        for (RegistryKey<Structure> structure : structures) {
            builder.criterion(structure.getValue().getPath(), TickCriterion.Conditions.createLocation(
                    LocationPredicate.Builder.createStructure(structure)
            ));
        }
        return builder;
    }

    public void generateStructureAdvancements(AdvancementEntry rootAdvancement, Consumer<AdvancementEntry> exporter) {
        AdvancementEntry medievalFortressAdvancement = this.requiredAllStructuresArrived(
                        Advancement.Builder.create(),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "medieval_fortress")
                        )
                )
                .parent(rootAdvancement)
                .display(
                        Items.STONE_BRICKS.getDefaultStack(),
                        Text.translatable("advancements.battlegrounds.medieval_fortress.title"),
                        Text.translatable("advancements.battlegrounds.medieval_fortress.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "battlegrounds:story/medieval_fortress");
        AdvancementEntry relicOfFantasyAdvancement = this.requiredAllStructuresArrived(
                        Advancement.Builder.create(),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "relic_of_fantasy")
                        )
                )
                .parent(rootAdvancement)
                .display(
                        Items.SMOOTH_QUARTZ.getDefaultStack(),
                        Text.translatable("advancements.battlegrounds.relic_of_fantasy.title"),
                        Text.translatable("advancements.battlegrounds.relic_of_fantasy.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "battlegrounds:story/relic_of_fantasy");
        AdvancementEntry wineShopAdvancement = this.requiredAllStructuresArrived(
                        Advancement.Builder.create(),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "wine_shop")
                        )
                )
                .parent(rootAdvancement)
                .display(
                        PotionUtil.setPotion(Items.POTION.getDefaultStack(), Potions.SWIFTNESS),
                        Text.translatable("advancements.battlegrounds.wine_shop.title"),
                        Text.translatable("advancements.battlegrounds.wine_shop.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "battlegrounds:story/wine_shop");
        AdvancementEntry libraryAdvancement = this.requiredAllStructuresArrived(
                        Advancement.Builder.create(),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "library")
                        )
                )
                .parent(rootAdvancement)
                .display(
                        Items.BOOKSHELF.getDefaultStack(),
                        Text.translatable("advancements.battlegrounds.library.title"),
                        Text.translatable("advancements.battlegrounds.library.description"),
                        null,
                        AdvancementFrame.TASK,
                        true, true, false
                )
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(exporter, "battlegrounds:story/library");
        AdvancementEntry allStructuresAdvancement = this.requiredAllStructuresArrived(
                        Advancement.Builder.create(),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "canopies")
                        ),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "desert_hut")
                        ),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "kiosk")
                        ),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "library")
                        ),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "medieval_library")
                        ),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "medieval_fortress")
                        ),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "relic_of_fantasy")
                        ),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "wine_shop")
                        ),
                        RegistryKey.of(
                                RegistryKeys.STRUCTURE,
                                new Identifier("battlegrounds", "ancient_ruins")
                        )
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

    public void generateItemAdvancements(AdvancementEntry rootAdvancement, Consumer<AdvancementEntry> exporter) {
        AdvancementEntry pvpProAdvancement = Advancement.Builder.create()
                .parent(rootAdvancement)
                .display(
                        Items.PVP_PRO_SWORD.getDefaultStack(),
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
                        Items.SEVEN_ELEVEN_SWORD.getDefaultStack(),
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
                        Items.STEVES_PAIN_SWORD.getDefaultStack(),
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
                        Items.CHANNELING_BOW.getDefaultStack(),
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
                        Items.BUTCHERS_AXE.getDefaultStack(),
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
