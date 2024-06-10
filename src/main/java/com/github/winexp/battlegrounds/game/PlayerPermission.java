package com.github.winexp.battlegrounds.game;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;

public class PlayerPermission {
    @NotNull
    public GameMode gameMode = GameMode.ADVENTURE;
    public boolean isDead = false;
    public boolean hasEnrichEffects = false;
    public int respawnChance = 1;

    public static PlayerPermission createFromNbt(NbtCompound nbt) {
        PlayerPermission permission = new PlayerPermission();
        permission.gameMode = GameMode.CODEC.parse(NbtOps.INSTANCE, nbt.get("gamemode"))
                .result().orElse(GameMode.ADVENTURE);
        permission.isDead = nbt.getBoolean("is_dead");
        permission.hasEnrichEffects = nbt.getBoolean("has_enrich_effects");
        permission.respawnChance = nbt.getInt("respawn_chance");
        return permission;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("gamemode", GameMode.CODEC.encodeStart(NbtOps.INSTANCE, this.gameMode).getOrThrow());
        nbt.putBoolean("is_dead", this.isDead);
        nbt.putBoolean("has_enrich_effects", this.hasEnrichEffects);
        nbt.putInt("respawn_chance", this.respawnChance);
        return nbt;
    }
}
