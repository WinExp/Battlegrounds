package com.github.winexp.battlegrounds.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.UUID;

public class PlayerUtil {
    public static UUID getAuthUUID(PlayerEntity player) {
        return player.getGameProfile().getId();
    }

    public static void broadcastPacket(Collection<UUID> players, CustomPayload payload) {
        for (UUID uuid : players) {
            ServerPlayerEntity player = Variables.server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }
}
