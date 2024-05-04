package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.util.time.Duration;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public record VoteSettings(BiConsumer<VoteInstance, CloseReason> voteClosedAction,
                           TriConsumer<VoteInstance, ServerPlayerEntity, Boolean> playerVotedAction,
                           VoteMode voteMode, Duration timeout, boolean allowChangeVote) {

    public enum VoteMode {
        ALL_ACCEPT(true, Integer::equals),
        OVER_HALF_ACCEPT(false, (total, current) -> (double) current / total > 0.5);

        public final boolean canDenyCancel;
        public final BiPredicate<Integer, Integer> acceptPredicate;

        VoteMode(boolean canDenyCancel, BiPredicate<Integer, Integer> acceptPredicate) {
            this.canDenyCancel = canDenyCancel;
            this.acceptPredicate = acceptPredicate;
        }
    }
}
