package com.github.winexp.battlegrounds.discussion.vote;

@FunctionalInterface
public interface VoteCallback {
    VoteCallback EMPTY = param1 -> {};

    default VoteCallback andThen(VoteCallback then) {
        return closeReason -> {
            this.onClosed(closeReason);
            then.onClosed(closeReason);
        };
    }

    void onClosed(Vote.CloseReason closeReason);
}
