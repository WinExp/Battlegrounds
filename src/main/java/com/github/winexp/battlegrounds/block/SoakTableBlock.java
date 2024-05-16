package com.github.winexp.battlegrounds.block;

import com.github.winexp.battlegrounds.block.entity.BlockEntityType;
import com.github.winexp.battlegrounds.block.entity.SoakTableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SoakTableBlock extends BlockWithEntity {
    public static final MapCodec<SoakTableBlock> CODEC = createCodec(SoakTableBlock::new);

    public SoakTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoakTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, BlockEntityType.SOAK_TABLE, SoakTableBlockEntity::tick);
    }
}
