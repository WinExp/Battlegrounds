package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.util.Constants;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;

public class PlayerPermission {
    @NotNull
    public GameMode gameMode = GameMode.ADVENTURE;
    public boolean inGame = false;
    public boolean allowNaturalRegen = true;
    public boolean hasEnrichEffects = false;

    public static PlayerPermission createFromNbt(NbtCompound nbt) {
        PlayerPermission permission = new PlayerPermission();
        permission.gameMode = GameMode.CODEC.parse(NbtOps.INSTANCE, nbt.get("gamemode"))
                .getOrThrow(false, Constants.LOGGER::error);
        permission.inGame = Codec.BOOL.parse(NbtOps.INSTANCE, nbt.get("in_game"))
                .getOrThrow(false, Constants.LOGGER::error);
        permission.allowNaturalRegen = Codec.BOOL.parse(NbtOps.INSTANCE, nbt.get("allow_natural_regen"))
                .getOrThrow(false, Constants.LOGGER::error);
        permission.hasEnrichEffects = Codec.BOOL.parse(NbtOps.INSTANCE, nbt.get("has_enrich_effects"))
                .getOrThrow(false, Constants.LOGGER::error);
        return permission;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("gamemode", GameMode.CODEC.encodeStart(NbtOps.INSTANCE, this.gameMode)
                .getOrThrow(false, Constants.LOGGER::error));
        nbt.put("in_game", Codec.BOOL.encodeStart(NbtOps.INSTANCE, this.inGame)
                .getOrThrow(false, Constants.LOGGER::error));
        nbt.put("allow_natural_regen", Codec.BOOL.encodeStart(NbtOps.INSTANCE, this.allowNaturalRegen)
                .getOrThrow(false, Constants.LOGGER::error));
        nbt.put("has_enrich_effects", Codec.BOOL.encodeStart(NbtOps.INSTANCE, this.hasEnrichEffects)
                .getOrThrow(false, Constants.LOGGER::error));
        return nbt;
    }
}
