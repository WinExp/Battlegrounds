package com.github.winexp.battlegrounds.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModServerPlayerEvents {
    public static Event<AllowNaturalRegen> ALLOW_NATURAL_REGEN = EventFactory.createArrayBacked(AllowNaturalRegen.class,
            (listeners) -> (player) -> {
                for (AllowNaturalRegen listener : listeners) {
                    if (!listener.isAllow(player)) return false;
                }
                return true;
            });
    public static Event<PlayerJoined> PLAYER_JOINED = EventFactory.createArrayBacked(PlayerJoined.class,
            (listeners) -> (connection, player, clientData) -> {
                for (PlayerJoined listener : listeners) {
                    listener.onPlayerJoined(connection, player, clientData);
                }
            });

    @FunctionalInterface
    public interface AllowNaturalRegen {
        boolean isAllow(PlayerEntity player);
    }

    @FunctionalInterface
    public interface PlayerJoined {
        void onPlayerJoined(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData);
    }
}
