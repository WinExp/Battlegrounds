package com.github.winexp.battlegrounds.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Blocks extends net.minecraft.block.Blocks {
    public static SoakTableBlock SOAK_TABLE = registerBlock("soak_table", new SoakTableBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASS).strength(3.0F).burnable()));

    private static <T extends Block> T registerBlock(String id, T block) {
        return Registry.register(Registries.BLOCK, new Identifier("battlegrounds", id), block);
    }

    public static void bootstrap() {
    }
}
