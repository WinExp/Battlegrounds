package com.github.winexp.battlegrounds.game;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.StringIdentifiable;

import java.util.function.BiPredicate;

public enum PVPMode implements StringIdentifiable {
    PEACEFUL(false),
    NO_PVP(((source, target) -> !(target instanceof PlayerEntity)
            || source.getSource() == null
            || !source.getSource().isPlayer())),
    PVP_MODE(true);

    public static final Codec<PVPMode> CODEC = StringIdentifiable.createCodec(PVPMode::values);
    private final BiPredicate<DamageSource, LivingEntity> allowDamagePredicate;

    PVPMode(boolean allow) {
        this.allowDamagePredicate = ((source, target) -> allow);
    }

    PVPMode(BiPredicate<DamageSource, LivingEntity> allowDamagePredicate) {
        this.allowDamagePredicate = allowDamagePredicate;
    }

    public boolean isAllowDamage(DamageSource source, LivingEntity target) {
        return this.allowDamagePredicate.test(source, target);
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
