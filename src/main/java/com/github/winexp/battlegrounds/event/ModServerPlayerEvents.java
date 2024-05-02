package com.github.winexp.battlegrounds.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModServerPlayerEvents {
    public static Event<AllowNaturalRegen> ALLOW_NATURAL_REGEN = EventFactory.createArrayBacked(AllowNaturalRegen.class,
            (listeners) -> (player) -> {
                for (AllowNaturalRegen listener : listeners) {
                    if (!listener.allowPlayerNaturalRegen(player)) return false;
                }
                return true;
            });
    public static Event<AfterDamaged> AFTER_PLAYER_DAMAGED = EventFactory.createArrayBacked(AfterDamaged.class,
            (listeners) -> (player, source, amount) -> {
                for (AfterDamaged listener : listeners) {
                    listener.onPlayerDamaged(player, source, amount);
                }
            });
    public static Event<AfterJoined> AFTER_PLAYER_JOINED = EventFactory.createArrayBacked(AfterJoined.class,
            (listeners) -> (connection, player, clientData) -> {
                for (AfterJoined listener : listeners) {
                    listener.onPlayerJoined(connection, player, clientData);
                }
            });

    @FunctionalInterface
    public interface AllowNaturalRegen {
        boolean allowPlayerNaturalRegen(ServerPlayerEntity player);
    }

    @FunctionalInterface
    public interface AfterDamaged {
        void onPlayerDamaged(ServerPlayerEntity player, DamageSource source, float amount);
    }

    @FunctionalInterface
    public interface AfterJoined {
        void onPlayerJoined(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData);
    }
}
