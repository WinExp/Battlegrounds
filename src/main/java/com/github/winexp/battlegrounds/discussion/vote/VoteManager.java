package com.github.winexp.battlegrounds.discussion.vote;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VoteManager {
    public static final VoteManager INSTANCE = new VoteManager(300);
    private final int maxVotes;

    private final Map<UUID, Vote> votes = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    protected VoteManager(int maxVotes) {
        this.maxVotes = maxVotes;
        new Thread(() -> {
            try {
                Thread.sleep(500);
                this.checkVotes();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    protected void checkVotes() {
        List<Vote> votes = new ObjectImmutableList<>(this.votes.values());
        for (Vote vote : votes) {
            if (!vote.isOpened()) this.votes.remove(vote.getUuid());
        }
    }

    public int getMaxVotes() {
        return this.maxVotes;
    }

    @Nullable
    public Vote getVote(UUID uuid) {
        return this.votes.get(uuid);
    }

    public Collection<Vote> getVotes() {
        return this.votes.values();
    }

    public boolean isExceedsLimit() {
        return this.votes.size() >= this.maxVotes;
    }

    protected boolean isValidVote(Vote vote) {
        return vote.isOpened();
    }

    public void putVote(@NotNull Vote vote) {
        Objects.requireNonNull(vote);
        if (!this.isValidVote(vote)) throw new IllegalStateException();
        if (!this.isExceedsLimit()) {
            this.votes.put(vote.getUuid(), vote);
        }
    }

    public void removeVote(UUID uuid) {
        this.votes.remove(uuid);
    }

    public int clearVotes() {
        this.closeAll();
        int size = this.votes.size();
        this.votes.clear();
        return size;
    }

    private void closeAll() {
        List<Vote> votes = new ObjectImmutableList<>(this.votes.values());
        for (Vote vote : votes) {
            vote.close();
        }
    }
}
