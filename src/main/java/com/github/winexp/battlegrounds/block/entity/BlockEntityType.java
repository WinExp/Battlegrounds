package com.github.winexp.battlegrounds.block.entity;

import com.github.winexp.battlegrounds.block.Blocks;
import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Set;

public class BlockEntityType<T extends BlockEntity> extends net.minecraft.block.entity.BlockEntityType<T> {
    public static final net.minecraft.block.entity.BlockEntityType<SoakTableBlockEntity> SOAK_TABLE = create("soak_table", Builder.create(SoakTableBlockEntity::new, Blocks.SOAK_TABLE));

    public BlockEntityType(BlockEntityFactory<? extends T> factory, Set<Block> blocks, Type<?> type) {
        super(factory, blocks, type);
    }

    private static <T extends BlockEntity> net.minecraft.block.entity.BlockEntityType<T> create(String id, Builder<T> builder) {
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("battlegrounds", id), builder.build(type));
    }

    public static void bootstrap() {
    }
}
