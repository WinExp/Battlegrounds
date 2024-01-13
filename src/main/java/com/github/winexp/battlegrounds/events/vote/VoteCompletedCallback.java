package com.github.winexp.battlegrounds.events.vote;

import com.github.winexp.battlegrounds.helper.VoteHelper;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface VoteCompletedCallback {
    enum Reason{
        ACCEPT, DENY, TIMEOUT, MANUAL
    }

    Event<VoteCompletedCallback> EVENT = EventFactory.createArrayBacked(VoteCompletedCallback.class,
            (listeners) -> (voter, reason) -> {
                for (VoteCompletedCallback listener : listeners){
                    ActionResult actionResult = listener.interact(voter, reason);
                    if (actionResult != ActionResult.PASS) return actionResult;
                }
                return ActionResult.PASS;
            });

    ActionResult interact(VoteHelper voter, Reason reason);
}
