package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.events.VoteEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class VoteManager {
    public final static VoteManager INSTANCE = new VoteManager();

    private final HashMap<Identifier, @NotNull VoteInstance> voteMap = new HashMap<>();

    public VoteManager() {
        VoteEvents.CLOSED.register(this::onVoteClosed);
    }

    private void onVoteClosed(VoteInstance voteInstance, VoteSettings.CloseReason reason) {
        try (voteInstance) {
            Identifier identifier = voteInstance.getIdentifier();
            this.voteMap.remove(identifier);
        }
    }

    public void forEach(BiConsumer<Identifier, VoteInstance> consumer) {
        this.voteMap.forEach(consumer);
    }

    public boolean openVote(VoteInstance voteInstance, Collection<ServerPlayerEntity> participants) {
        Identifier identifier = voteInstance.getIdentifier();
        if (this.voteMap.containsKey(identifier)) return false;
        voteInstance.openVote(participants);
        this.voteMap.put(identifier, voteInstance);
        return true;
    }

    public boolean closeVote(Identifier identifier) {
        if (!voteMap.containsKey(identifier)) return false;
        VoteInstance voteInstance = this.voteMap.get(identifier);
        voteInstance.closeVote(VoteSettings.CloseReason.MANUAL);
        return true;
    }
}
