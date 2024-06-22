package com.github.winexp.battlegrounds.world.gen.structure.processor;

import com.github.winexp.battlegrounds.block.Blocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.Identifier;

public class StructureProcessorLists extends net.minecraft.structure.processor.StructureProcessorLists {
    public static final RegistryKey<StructureProcessorList> MEDIEVAL_FORTRESS = of("medieval_fortress");
    public static final RegistryKey<StructureProcessorList> MEDIEVAL_LIBRARY = of("medieval_library");
    public static final RegistryKey<StructureProcessorList> RELIC_OF_FANTASY = of("relic_of_fantasy");

    private static RegistryKey<StructureProcessorList> of(String id) {
        return RegistryKey.of(RegistryKeys.PROCESSOR_LIST, new Identifier("battlegrounds", id));
    }

    public static void bootstrap(Registerable<StructureProcessorList> structureProcessorListRegisterable) {
        structureProcessorListRegisterable.register(MEDIEVAL_FORTRESS, new StructureProcessorList(ImmutableList.of(new BlockAgeStructureProcessor(0.1F))));
        structureProcessorListRegisterable.register(MEDIEVAL_LIBRARY, new StructureProcessorList(ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.LIGHT_BLUE_CARPET, 0.2F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.AIR.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.POLISHED_ANDESITE, 0.27F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.ANDESITE.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILES, 0.2F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.COBBLED_DEEPSLATE.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILES, 0.13F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.AIR.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILE_STAIRS, 0.26F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.COBBLED_DEEPSLATE_SLAB.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILE_STAIRS, 0.13F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.AIR.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.DEEPSLATE_TILE_WALL, 0.13F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.COBBLED_DEEPSLATE_WALL.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.STONE_BRICKS, 0.18F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.CRACKED_STONE_BRICKS.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.STONE_BRICKS, 0.1F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.MOSSY_STONE_BRICKS.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.STONE_BRICK_STAIRS, 0.27F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.SPRUCE_STAIRS, 0.25F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.SPRUCE_SLAB.getDefaultState()
                )
        )))));
        structureProcessorListRegisterable.register(RELIC_OF_FANTASY, new StructureProcessorList(ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.SMOOTH_QUARTZ, 0.13F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.AIR.getDefaultState()
                ),
                new StructureProcessorRule(
                        new RandomBlockMatchRuleTest(Blocks.GLASS, 0.1F),
                        AlwaysTrueRuleTest.INSTANCE,
                        Blocks.AIR.getDefaultState()
                )
        )))));
    }
}
