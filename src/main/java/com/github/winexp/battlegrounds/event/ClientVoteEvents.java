package com.github.winexp.battlegrounds.event;

import com.github.winexp.battlegrounds.discussion.vote.CloseReason;
import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ClientVoteEvents {
    public static Event<Opened> OPENED = EventFactory.createArrayBacked(Opened.class,
            (listeners) -> (voteInstance) -> {
                for (Opened listener : listeners) {
                    listener.onOpened(voteInstance);
                }
            });
    public static Event<Closed> CLOSED = EventFactory.createArrayBacked(Closed.class,
            (listeners) -> (voteInfo, reason) -> {
                for (Closed listener : listeners) {
                    listener.onClosed(voteInfo, reason);
                }
            });
    public static Event<PlayerVoted> PLAYER_VOTED = EventFactory.createArrayBacked(PlayerVoted.class,
            (listeners) -> (playerName, voteInfo, result) -> {
                for (PlayerVoted listener : listeners) {
                    listener.onPlayerVoted(playerName, voteInfo, result);
                }
            });

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface Opened {
        void onOpened(VoteInfo voteInfo);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface Closed {
        void onClosed(VoteInfo voteInfo, CloseReason reason);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface PlayerVoted {
        void onPlayerVoted(Text playerName, VoteInfo voteInfo, boolean result);
    }
}
