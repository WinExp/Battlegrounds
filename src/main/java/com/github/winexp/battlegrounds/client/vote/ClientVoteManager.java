package com.github.winexp.battlegrounds.client.vote;

import com.github.winexp.battlegrounds.discussion.vote.Vote;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.QueryVotePayloadC2S;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.SyncVotesPayloadC2S;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.VotePayloadC2S;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ClientVoteManager extends VoteManager {
    public static final ClientVoteManager INSTANCE = new ClientVoteManager();
    private final List<Listener> listeners = new ObjectArrayList<>();

    protected ClientVoteManager() {
        super(VoteManager.INSTANCE.getMaxVotes());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> this.clearVotes());
    }

    public void registerListener(Listener listener) {
        this.listeners.add(listener);
    }

    private void trigListeners(Consumer<Listener> action) {
        synchronized (this.listeners) {
            for (Listener listener : this.listeners) {
                action.accept(listener);
            }
        }
    }

    @Override
    protected boolean isValidVote(Vote vote) {
        return true;
    }

    public void onSyncVotes(Collection<Vote> votes) {
        this.replaceVotes(votes);
        this.trigListeners(listener -> listener.onSyncVotes(votes));
    }

    public void syncVotes() {
        ClientPlayNetworking.send(new SyncVotesPayloadC2S());
    }

    public void queryVote(UUID uuid) {
        ClientPlayNetworking.send(new QueryVotePayloadC2S(uuid));
    }

    public void onVoteUpdated(UUID uuid, @Nullable Vote vote) {
        if (vote == null) {
            Vote removedVote = this.getVote(uuid);
            if (removedVote != null) {
                this.removeVote(uuid);
                this.trigListeners(listener -> listener.onVoteRemoved(uuid));
                removedVote.close();
            }
        }
        else if (this.getVote(uuid) == null) {
            vote.addCallback(closeReason -> this.queryVote(uuid));
            this.putVote(vote);
            this.trigListeners(listener -> listener.onVoteAdded(vote));
        } else {
            this.putVote(vote);
            this.trigListeners(listener -> listener.onVoteUpdated(vote));
        }
    }

    private void replaceVotes(Collection<Vote> votes) {
        this.clearVotes();
        this.putAllVotes(votes);
    }

    private void putAllVotes(Collection<Vote> votes) {
        for (Vote vote : votes) {
            this.putVote(vote);
        }
    }

    public void vote(UUID uuid, boolean result) {
        ClientPlayNetworking.send(new VotePayloadC2S(uuid, result));
    }

    public interface Listener {
        void onSyncVotes(Collection<Vote> votes);

        void onVoteAdded(Vote vote);

        void onVoteUpdated(Vote vote);

        void onVoteRemoved(UUID uuid);
    }
}
