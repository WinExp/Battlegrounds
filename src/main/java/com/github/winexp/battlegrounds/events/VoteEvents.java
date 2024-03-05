package com.github.winexp.battlegrounds.events;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.discussion.vote.VoteSettings;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface VoteEvents {
    Event<VoteEvents> CLOSED = EventFactory.createArrayBacked(VoteEvents.class,
            (listeners) -> (voteInstance, reason) -> {
                for (VoteEvents listener : listeners) {
                    listener.interact(voteInstance, reason);
                }
            });

    void interact(VoteInstance voteInstance, VoteSettings.CloseReason reason);
}
