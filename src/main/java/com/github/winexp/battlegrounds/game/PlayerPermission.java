package com.github.winexp.battlegrounds.game;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Util;
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
        permission.gameMode = Util.getResult(GameMode.CODEC.parse(NbtOps.INSTANCE, nbt.get("gamemode")), IllegalStateException::new);
        permission.inGame = Util.getResult(Codec.BOOL.parse(NbtOps.INSTANCE, nbt.get("in_game")), IllegalStateException::new);
        permission.allowNaturalRegen = Util.getResult(Codec.BOOL.parse(NbtOps.INSTANCE, nbt.get("allow_natural_regen")), IllegalStateException::new);
        permission.hasEnrichEffects = Util.getResult(Codec.BOOL.parse(NbtOps.INSTANCE, nbt.get("has_enrich_effects")), IllegalStateException::new);
        return permission;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("gamemode", Util.getResult(GameMode.CODEC.encodeStart(NbtOps.INSTANCE, this.gameMode), IllegalStateException::new));
        nbt.put("in_game", Util.getResult(Codec.BOOL.encodeStart(NbtOps.INSTANCE, this.inGame), IllegalStateException::new));
        nbt.put("allow_natural_regen", Util.getResult(Codec.BOOL.encodeStart(NbtOps.INSTANCE, this.allowNaturalRegen), IllegalStateException::new));
        nbt.put("has_enrich_effects", Util.getResult(Codec.BOOL.encodeStart(NbtOps.INSTANCE, this.hasEnrichEffects), IllegalStateException::new));
        return nbt;
    }
}
