package com.github.winexp.battlegrounds.game;

import com.mojang.serialization.Codec;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;

import java.util.function.BiPredicate;

public enum PVPMode implements StringIdentifiable {
    PEACEFUL(false),
    NO_PVP((source, target) -> source.getSource() == null
            || !source.getSource().isPlayer()),
    PVP_MODE(true);

    public static final Codec<PVPMode> CODEC = StringIdentifiable.createCodec(PVPMode::values);
    private final BiPredicate<DamageSource, ServerPlayerEntity> allowDamagePredicate;

    PVPMode(boolean allowDamage) {
        this.allowDamagePredicate = ((source, target) -> allowDamage);
    }

    PVPMode(BiPredicate<DamageSource, ServerPlayerEntity> allowDamagePredicate) {
        this.allowDamagePredicate = allowDamagePredicate;
    }

    public boolean isAllowDamage(DamageSource source, ServerPlayerEntity target) {
        return this.allowDamagePredicate.test(source, target);
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
