package com.github.winexp.battlegrounds.events.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface PlayerJoinedGameCallback {
    Event<PlayerJoinedGameCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinedGameCallback.class,
            (listeners) -> (connection, player, clientData, cir) -> {
                for (PlayerJoinedGameCallback listener : listeners){
                    ActionResult actionResult = listener.interact(connection, player, clientData, cir);

                    if (actionResult != ActionResult.PASS) return actionResult;
                }

                return ActionResult.PASS;
            });

    ActionResult interact(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfoReturnable<Text> cir);
}
