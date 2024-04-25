package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.game.GameStage;
import com.github.winexp.battlegrounds.game.PlayerPermission;
import com.github.winexp.battlegrounds.util.data.ModVersion;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class PlayerUtil {
    private static final Map<UUID, ModVersion> playerVersionMap = new HashMap<>();

    public static void kickAllPlayers(MinecraftServer server, Text message) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.networkHandler.disconnect(message);
        }
    }

    public static void randomTpAllPlayers(MinecraftServer server, World world) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            randomTeleport(world, player);
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

    public static void broadcastSound(MinecraftServer server, SoundEvent sound, float volume, float pitch) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.playSound(sound, volume, pitch);
        }
    }

    public static void broadcastTitle(MinecraftServer server, Text title) {
        broadcastTitle(server, title, Text.empty());
    }

    public static void broadcastTitle(MinecraftServer server, Text title, Text subtitle) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            sendTitle(player, title, subtitle);
        }
    }

    public static <T extends CustomPayload> void broadcastPacket(MinecraftServer server, T packet) {
        broadcastPacket(server, packet, (p, player) -> {});
    }

    public static <T extends CustomPayload> void broadcastPacket(MinecraftServer server, T packet, BiConsumer<T, ServerPlayerEntity> preprocessor) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            preprocessor.accept(packet, player);
            ServerPlayNetworking.send(player, packet);
        }
    }

    public static void randomTeleport(World world, ServerPlayerEntity player) {
        BlockPos pos = RandomUtil.getSecureLocation(world);
        Vec3d centerPos = pos.toCenterPos();
        player.teleport((ServerWorld) world, centerPos.getX(), centerPos.getY(), centerPos.getZ(), 0, 0);
        player.onLanding();
    }

    public static ModVersion getPlayerModVersion(@NotNull UUID uuid) {
        return playerVersionMap.get(uuid);
    }

    public static void setPlayerModVersion(@NotNull UUID uuid, @Nullable ModVersion modVersion) {
        if (modVersion == null) playerVersionMap.remove(uuid);
        else playerVersionMap.put(uuid, modVersion);
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
