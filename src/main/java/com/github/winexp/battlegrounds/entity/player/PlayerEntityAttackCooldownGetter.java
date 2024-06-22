package com.github.winexp.battlegrounds.entity.player;

public interface PlayerEntityAttackCooldownGetter {
    default float battlegrounds$getLastAttackCooldown() {
        return 1.0F;
    }
}
