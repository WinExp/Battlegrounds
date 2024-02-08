package com.github.winexp.battlegrounds.events.vote;

import com.github.winexp.battlegrounds.helper.VoteHelper;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerVotedCallback {
    Event<PlayerVotedCallback> EVENT = EventFactory.createArrayBacked(PlayerVotedCallback.class,
            (listeners) -> (voter, player, result) -> {
                for (PlayerVotedCallback listener : listeners) {
                    ActionResult actionResult = listener.interact(voter, player, result);
                    if (actionResult != ActionResult.PASS) return actionResult;
                }
                return ActionResult.PASS;
            });

    ActionResult interact(VoteHelper voter, ServerPlayerEntity player, boolean result);
}
