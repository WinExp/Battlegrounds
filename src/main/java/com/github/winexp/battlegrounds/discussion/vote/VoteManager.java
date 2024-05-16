package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.event.ModServerPlayerEvents;
import com.github.winexp.battlegrounds.event.ServerVoteEvents;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.PlayerVotedPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.SyncVoteInfosPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.VoteClosedPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.VoteOpenedPayloadS2C;
import com.github.winexp.battlegrounds.util.Variables;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class VoteManager {
    public static final VoteManager INSTANCE = new VoteManager();

    private final Map<Identifier, VoteInstance> voteMap = new ConcurrentHashMap<>();

    protected VoteManager() {
        ServerVoteEvents.OPENED.register(this::onVoteOpened);
        ServerVoteEvents.CLOSED.register(this::onVoteClosed);
        ServerVoteEvents.PLAYER_VOTED.register(this::onPlayerVoted);
        ModServerPlayerEvents.AFTER_PLAYER_JOINED.register(this::onPlayerJoined);
    }

    private void onVoteOpened(VoteInstance voteInstance) {
        VoteOpenedPayloadS2C packet = new VoteOpenedPayloadS2C(voteInstance);
        for (UUID uuid : voteInstance.getParticipants()) {
            ServerPlayerEntity player = Variables.server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                ServerPlayNetworking.send(player, packet);
            }
        }
    }

    private void onVoteClosed(VoteInstance voteInstance, CloseReason reason) {
        Identifier identifier = voteInstance.getIdentifier();
        this.voteMap.remove(identifier);
        VoteClosedPayloadS2C packet = new VoteClosedPayloadS2C(voteInstance, reason);
        for (UUID uuid : voteInstance.getParticipants()) {
            ServerPlayerEntity player = Variables.server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                ServerPlayNetworking.send(player, packet);
            }
        }
    }

    private void onPlayerVoted(ServerPlayerEntity player, VoteInstance voteInstance, boolean result) {
        PlayerVotedPayloadS2C packet = new PlayerVotedPayloadS2C(player.getDisplayName(), voteInstance, result);
        for (UUID uuid : voteInstance.getParticipants()) {
            ServerPlayerEntity player1 = Variables.server.getPlayerManager().getPlayer(uuid);
            if (player1 != null) {
                ServerPlayNetworking.send(player1, packet);
            }
        }
    }

    private void onPlayerJoined(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData) {
        this.syncVoteInfos(player);
    }

    public void syncVoteInfos(ServerPlayerEntity player) {
        List<VoteInstance> voteInstances = this.getVoteList();
        SyncVoteInfosPayloadS2C packet = new SyncVoteInfosPayloadS2C(voteInstances);
        ServerPlayNetworking.send(player, packet);
    }

    public boolean containsVote(Identifier identifier) {
        return this.voteMap.containsKey(identifier);
    }

    public boolean isVoting(Identifier identifier) {
        return this.containsVote(identifier) && this.voteMap.get(identifier).isVoting();
    }

    public List<VoteInstance> getVoteList() {
        return List.copyOf(this.voteMap.values());
    }

    public Optional<VoteInstance> getVoteInstance(Identifier identifier) {
        return Optional.ofNullable(this.voteMap.get(identifier));
    }

    public void forEachVotes(BiConsumer<Identifier, VoteInstance> consumer) {
        this.voteMap.forEach(consumer);
    }

    public boolean openVote(VoteInstance voteInstance, Collection<ServerPlayerEntity> participants) {
        Identifier identifier = voteInstance.getIdentifier();
        if (this.isVoting(identifier)) return false;
        if (!voteInstance.openVote(participants)) return false;
        this.voteMap.put(identifier, voteInstance);
        return true;
    }

    public boolean closeVote(Identifier identifier) {
        if (!this.isVoting(identifier)) return false;
        VoteInstance voteInstance = this.voteMap.get(identifier);
        return voteInstance.closeVote(CloseReason.MANUAL);
    }

    public void closeAllVotes() {
        this.forEachVotes((id, vote) -> this.closeVote(id));
    }
}
