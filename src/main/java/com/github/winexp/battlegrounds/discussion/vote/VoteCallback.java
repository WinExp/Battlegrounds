package com.github.winexp.battlegrounds.discussion.vote;

import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.function.BiConsumer;

public record VoteCallback(PlayerVotedAction playerVotedAction,
                           ClosedAction closedAction) {
    public static VoteCallback EMPTY = new VoteCallback(PlayerVotedAction.EMPTY, ClosedAction.EMPTY);

    @FunctionalInterface
    public interface PlayerVotedAction extends TriConsumer<VoteInstance, ServerPlayerEntity, Boolean> {
        PlayerVotedAction EMPTY = (param1, param2, param3) -> {};
    }

    @FunctionalInterface
    public interface ClosedAction extends BiConsumer<VoteInstance, CloseReason> {
        ClosedAction EMPTY = (param1, param2) -> {};
    }

    public static VoteCallback onlyOnClosed(ClosedAction action) {
        return new VoteCallback(PlayerVotedAction.EMPTY, action);
    }
}
