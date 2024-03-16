package com.github.winexp.battlegrounds.event;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.discussion.vote.VoteSettings;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ServerVoteEvents {
    public static Event<Opened> OPENED = EventFactory.createArrayBacked(Opened.class,
            (listeners) -> (voteInstance) -> {
                for (Opened listener : listeners) {
                    listener.onOpened(voteInstance);
                }
            });
    public static Event<Closed> CLOSED = EventFactory.createArrayBacked(Closed.class,
            (listeners) -> (voteInstance, reason) -> {
                for (Closed listener : listeners) {
                    listener.onClosed(voteInstance, reason);
                }
            });

    @FunctionalInterface
    public interface Opened {
        void onOpened(VoteInfo voteInfo);
    }

    @FunctionalInterface
    public interface Closed {
        void onClosed(VoteInfo voteInfo, VoteSettings.CloseReason reason);
    }
}
