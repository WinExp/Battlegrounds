package com.github.winexp.battlegrounds.events;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.discussion.vote.VoteSettings;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface VoteEvents {
    Event<Closed> CLOSED = EventFactory.createArrayBacked(Closed.class,
            (listeners) -> (voteInstance, reason) -> {
                for (Closed listener : listeners) {
                    listener.closed(voteInstance, reason);
                }
            });

    @FunctionalInterface
    interface Closed {
        void closed(VoteInstance voteInstance, VoteSettings.CloseReason reason);
    }
}
