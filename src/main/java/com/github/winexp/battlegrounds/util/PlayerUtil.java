package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.game.GameStage;
import com.github.winexp.battlegrounds.game.PlayerPermission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.Predicate;

public class PlayerUtil {
    public static void kickAllPlayers(MinecraftServer server, Text message) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.networkHandler.disconnect(message);
        }
    }

    public static void sendTitle(ServerPlayerEntity player, Text title) {
        sendTitle(player, title, Text.empty());
    }

    public static void sendTitle(ServerPlayerEntity player, Text title, Text subtitle) {
        player.networkHandler.sendPacket(new TitleS2CPacket(title));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(subtitle));
        player.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 10, 15));
    }

    public static void broadcastSound(MinecraftServer server, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.playSound(sound, category, volume, pitch);
        }
    }

    public static void broadcastTitle(MinecraftServer server, Text title) {
        broadcastTitle(server, title, (player) -> true);
    }

    public static void broadcastTitle(MinecraftServer server, Text title, Predicate<ServerPlayerEntity> playerPredicate) {
        broadcastTitle(server, title, Text.empty(), playerPredicate);
    }

    public static void broadcastTitle(MinecraftServer server, Text title, Text subtitle) {
        broadcastTitle(server, title, subtitle, (player) -> true);
    }

    public static void broadcastTitle(MinecraftServer server, Text title, Text subtitle, Predicate<ServerPlayerEntity> playerPredicate) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (playerPredicate.test(player)) {
                sendTitle(player, title, subtitle);
            }
        }
    }

    public static void randomTeleport(World world, ServerPlayerEntity player) {
        BlockPos pos = RandomUtil.getSecureLocation(world);
        player.teleport((ServerWorld) world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
    }

    public static void setGameModeToMap(ServerPlayerEntity player, GameMode gameMode) {
        UUID uuid = getAuthUUID(player);
        PlayerPermission permission = Variables.gameManager.getPlayerPermission(uuid);
        if (permission == null) {
            permission = new PlayerPermission();
            permission.gameMode = gameMode;
            Variables.gameManager.setPlayerPermission(getAuthUUID(player), permission);
        } else {
            permission.gameMode = gameMode;
        }
    }

    public static void changeGameMode(ServerPlayerEntity player, GameMode gameMode) {
        player.changeGameMode(gameMode);
        setGameModeToMap(player, gameMode);
    }

    public static GameMode getDefaultGameMode() {
        GameMode gameMode;
        if (Variables.gameManager.getGameStage() == GameStage.IDLE) {
            gameMode = GameMode.ADVENTURE;
        } else {
            gameMode = GameMode.SPECTATOR;
        }
        return gameMode;
    }

    public static void changeGameModeWithMap(ServerPlayerEntity player) {
        UUID uuid = getAuthUUID(player);
        GameMode defaultGameMode = getDefaultGameMode();
        PlayerPermission permission = Variables.gameManager.getPlayerPermission(uuid);
        if (permission == null) {
            player.changeGameMode(defaultGameMode);
        } else {
            player.changeGameMode(permission.gameMode);
        }
    }

    public static UUID getAuthUUID(PlayerEntity player) {
        return player.getGameProfile().getId();
    }
}
