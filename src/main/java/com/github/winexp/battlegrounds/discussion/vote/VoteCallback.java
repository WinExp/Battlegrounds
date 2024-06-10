package com.github.winexp.battlegrounds.discussion.vote;

import net.minecraft.server.network.ServerPlayerEntity;

public interface VoteCallback {
    VoteCallback EMPTY = new VoteCallback() {
        @Override
        public void onPlayerVoted(VoteInstance voteInstance, ServerPlayerEntity player, boolean result) {
        }

        @Override
        public void onClosed(VoteInstance voteInstance, CloseReason closeReason) {
        }
    };

    void onPlayerVoted(VoteInstance voteInstance, ServerPlayerEntity player, boolean result);

    void onClosed(VoteInstance voteInstance, CloseReason closeReason);
}
