package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.util.function.TripleConsumer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public record VoteSettings(BiConsumer<VoteInstance, CloseReason> voteClosedAction, TripleConsumer<VoteInstance, ServerPlayerEntity, Boolean> playerVotedAction,
                           VoteMode voteMode, long timeout) {
    public static final long INFINITE_TIME = -1;

    public enum CloseReason {
        ACCEPTED, DENIED, TIMEOUT, MANUAL
    }

    public enum VoteMode {
        ALL_ACCEPT(true, false, Integer::equals),
        OVER_HALF_ACCEPT(false, false, (total, current) -> (double) current / total > 0.5);

        public final boolean canDenyCancel;
        public final boolean allowChangeVote;
        public final BiPredicate<Integer, Integer> acceptPredicate;

        VoteMode(boolean canDenyCancel, boolean allowChangeVote, BiPredicate<Integer, Integer> acceptPredicate) {
            this.canDenyCancel = canDenyCancel;
            this.allowChangeVote = allowChangeVote;
            this.acceptPredicate = acceptPredicate;
        }
    }
}
