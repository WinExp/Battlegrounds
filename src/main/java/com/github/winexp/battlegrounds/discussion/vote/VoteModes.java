package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.registry.ModRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class VoteModes {
    public static final VoteMode ALL_ACCEPT = register("all_accept", new VoteMode() {
        @Override
        public boolean canAccept(Vote vote) {
            return vote.getAcceptedCount() == vote.getParticipantsCount();
        }

        @Override
        public boolean canTerminate(Vote vote) {
            return vote.getDeniedCount() > 0;
        }
    });
    public static final VoteMode OVER_HALF_ACCEPT = register("over_half_accept", new VoteMode() {
        @Override
        public boolean canAccept(Vote vote) {
            return vote.getAcceptedCount() * 2 > vote.getParticipantsCount();
        }

        @Override
        public boolean canTerminate(Vote vote) {
            return vote.getDeniedCount() * 2 >= vote.getParticipantsCount();
        }
    });

    private static VoteMode register(String id, VoteMode voteMode) {
        return Registry.register(ModRegistries.VOTE_MODE, new Identifier("battlegrounds", id), voteMode);
    }

    public static void bootstrap() {
    }
}
