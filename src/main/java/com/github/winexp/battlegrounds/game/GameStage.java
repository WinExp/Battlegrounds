package com.github.winexp.battlegrounds.game;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum GameStage implements StringIdentifiable {
    IDLE, WAITING_PLAYER, GAMING, FINAL;

    public static final Codec<GameStage> CODEC = StringIdentifiable.createCodec(GameStage::values);

    public boolean isGaming() {
        return this.ordinal() >= GAMING.ordinal();
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
