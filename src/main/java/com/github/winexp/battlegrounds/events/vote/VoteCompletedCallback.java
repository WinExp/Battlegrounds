package com.github.winexp.battlegrounds.events.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteHelper;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

@FunctionalInterface
public interface VoteCompletedCallback {
    Event<VoteCompletedCallback> EVENT = EventFactory.createArrayBacked(VoteCompletedCallback.class,
            (listeners) -> (voter, reason) -> {
                for (VoteCompletedCallback listener : listeners) {
                    ActionResult actionResult = listener.interact(voter, reason);
                    if (actionResult != ActionResult.PASS) return actionResult;
                }
                return ActionResult.PASS;
            });

    ActionResult interact(VoteHelper voter, Reason reason);

    enum Reason {
        ACCEPT, DENY, TIMEOUT, MANUAL
    }
}
