package com.github.winexp.battlegrounds.events.vote;

import com.github.winexp.battlegrounds.helper.VoteHelper;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerVotedCallback {
    Event<PlayerVotedCallback> EVENT = EventFactory.createArrayBacked(PlayerVotedCallback.class,
            (listeners) -> (player, voter, result) -> {
                for (PlayerVotedCallback listener : listeners){
                    ActionResult actionResult = listener.interact(player, voter, result);
                    if (actionResult != ActionResult.PASS) return actionResult;
                }
                return ActionResult.PASS;
            });

    ActionResult interact(ServerPlayerEntity player, VoteHelper voter, boolean result);
}
