package com.github.winexp.battlegrounds.event;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.discussion.vote.VoteSettings;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerVoteEvents {
    public static Event<Opened> OPENED = EventFactory.createArrayBacked(Opened.class,
            (listeners) -> (voteInfo) -> {
                for (Opened listener : listeners) {
                    listener.onOpened(voteInfo);
                }
            });
    public static Event<Closed> CLOSED = EventFactory.createArrayBacked(Closed.class,
            (listeners) -> (voteInfo, reason) -> {
                for (Closed listener : listeners) {
                    listener.onClosed(voteInfo, reason);
                }
            });
    public static Event<PlayerVoted> PLAYER_VOTED = EventFactory.createArrayBacked(PlayerVoted.class,
            (listeners) -> (player, voteInfo, result) -> {
                for (PlayerVoted listener : listeners) {
                    listener.onPlayerVoted(player, voteInfo, result);
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

    @FunctionalInterface
    public interface PlayerVoted {
        void onPlayerVoted(ServerPlayerEntity player, VoteInfo voteInfo, boolean result);
    }
}
