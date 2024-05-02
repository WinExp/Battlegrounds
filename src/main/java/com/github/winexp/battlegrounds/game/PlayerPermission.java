package com.github.winexp.battlegrounds.game;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;

public class PlayerPermission {
    @NotNull
    public GameMode gameMode = GameMode.ADVENTURE;
    public boolean inGame = false;
    public boolean hasEnrichEffects = false;
    public int respawnChance = 1;

    public static PlayerPermission createFromNbt(NbtCompound nbt) {
        PlayerPermission permission = new PlayerPermission();
        permission.gameMode = Util.getResult(GameMode.CODEC.parse(NbtOps.INSTANCE, nbt.get("gamemode")), IllegalStateException::new);
        permission.inGame = nbt.getBoolean("in_game");
        permission.hasEnrichEffects = nbt.getBoolean("has_enrich_effects");
        permission.respawnChance = nbt.getInt("respawn_chance");
        return permission;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("gamemode", Util.getResult(GameMode.CODEC.encodeStart(NbtOps.INSTANCE, this.gameMode), IllegalStateException::new));
        nbt.putBoolean("in_game", this.inGame);
        nbt.putBoolean("has_enrich_effects", this.hasEnrichEffects);
        nbt.putInt("respawn_chance", this.respawnChance);
        return nbt;
    }
}
