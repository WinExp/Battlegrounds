package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.configs.GameProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.UUID;

public class PlayerUtil {
    public static void sendTitle(ServerPlayerEntity player, Text title) {
        sendTitle(player, title, Text.of(""));
    }

    public static void sendTitle(ServerPlayerEntity player, Text title, Text subtitle) {
        player.networkHandler.sendPacket(new TitleS2CPacket(title));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(subtitle));
        player.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 10, 15));
    }

    public static void randomTeleport(World world, ServerPlayerEntity player) {
        BlockPos pos = RandomUtil.getSecureLocation(world);
        player.teleport((ServerWorld) world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
    }

    public static void setGameModeMap(ServerPlayerEntity player, GameMode gameMode) {
        if (Variables.progress.players.get(getUUID(player)) == null) {
            GameProgress.PlayerPermission permission = new GameProgress.PlayerPermission();
            permission.gameMode = gameMode;
            Variables.progress.players.put(getUUID(player), permission);
        } else {
            Variables.progress.players.get(getUUID(player)).gameMode = gameMode;
        }
    }

    public static void setGameMode(ServerPlayerEntity player, GameMode gameMode) {
        player.changeGameMode(gameMode);
        setGameModeMap(player, gameMode);
    }

    public static GameMode getDefaultGameMode() {
        GameMode gameMode;
        if (Variables.progress.gameStage.isIdle()) {
            gameMode = GameMode.ADVENTURE;
        } else {
            gameMode = GameMode.SPECTATOR;
        }
        return gameMode;
    }

    public static void setGameModeWithMap(ServerPlayerEntity player) {
        GameMode defaultGameMode = getDefaultGameMode();
        if (Variables.progress.players.get(getUUID(player)) == null) {
            player.changeGameMode(defaultGameMode);
        } else {
            player.changeGameMode(Variables.progress.players.get(getUUID(player)).gameMode);
        }
    }

    public static UUID getUUID(PlayerEntity player) {
        return player.getGameProfile().getId();
    }
}
