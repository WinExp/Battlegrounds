package com.github.winexp.battlegrounds.game;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum GameBorderStage implements StringIdentifiable {
    WAITING, RESIZING;

    public static final Codec<GameBorderStage> CODEC = StringIdentifiable.createCodec(GameBorderStage::values);

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
