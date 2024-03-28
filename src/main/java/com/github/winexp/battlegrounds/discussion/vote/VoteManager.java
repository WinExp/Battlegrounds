package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.event.ModServerPlayerEvents;
import com.github.winexp.battlegrounds.event.ServerVoteEvents;
import com.github.winexp.battlegrounds.network.packet.s2c.play.vote.PlayerVotedS2CPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.play.vote.SyncVoteInfosS2CPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.play.vote.VoteClosedS2CPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.play.vote.VoteOpenedS2CPacket;
import com.github.winexp.battlegrounds.util.PlayerUtil;
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

    private final ConcurrentHashMap<Identifier, VoteInstance> voteMap = new ConcurrentHashMap<>();

    protected VoteManager() {
        ServerVoteEvents.OPENED.register(this::onVoteOpened);
        ServerVoteEvents.CLOSED.register(this::onVoteClosed);
        ServerVoteEvents.PLAYER_VOTED.register(this::onPlayerVoted);
        ModServerPlayerEvents.PLAYER_JOINED.register(this::onPlayerJoined);
    }

    private void onVoteOpened(VoteInfo voteInfo) {
        VoteOpenedS2CPacket packet = new VoteOpenedS2CPacket(voteInfo);
        PlayerUtil.broadcastPacket(Variables.server, packet);
    }

    private void onVoteClosed(VoteInfo voteInfo, VoteSettings.CloseReason reason) {
        Identifier identifier = voteInfo.identifier;
        this.voteMap.remove(identifier);
        VoteClosedS2CPacket packet = new VoteClosedS2CPacket(voteInfo, reason);
        PlayerUtil.broadcastPacket(Variables.server, packet);
    }

    private void onPlayerVoted(ServerPlayerEntity player, VoteInfo voteInfo, boolean result) {
        PlayerVotedS2CPacket packet = new PlayerVotedS2CPacket(player.getDisplayName(), voteInfo, result);
        PlayerUtil.broadcastPacket(Variables.server, packet);
    }

    private void onPlayerJoined(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData) {
        this.syncVoteInfos(player);
    }

    public void syncVoteInfos(ServerPlayerEntity player) {
        Collection<VoteInstance> votes = this.getVoteList();
        ArrayList<VoteInfo> voteInfos = new ArrayList<>();
        for (VoteInstance vote : votes) {
            VoteInfo voteInfo = vote.getVoteInfo(player);
            voteInfos.add(voteInfo);
        }
        SyncVoteInfosS2CPacket packet = new SyncVoteInfosS2CPacket(voteInfos);
        ServerPlayNetworking.send(player, packet);
    }

    public boolean containsVote(Identifier identifier) {
        return this.voteMap.containsKey(identifier);
    }

    public boolean isVoting(Identifier identifier) {
        return this.containsVote(identifier) && this.voteMap.get(identifier).isVoting();
    }

    public Collection<VoteInstance> getVoteList() {
        return this.voteMap.values();
    }

    public Optional<VoteInstance> getVoteInstance(Identifier identifier) {
        return Optional.ofNullable(this.voteMap.get(identifier));
    }

    public void forEachVotes(BiConsumer<Identifier, VoteInstance> consumer) {
        this.voteMap.forEach(consumer);
    }


    public Optional<VoteInstance> openVoteWithPreset(VotePreset preset, Collection<ServerPlayerEntity> participants, Map<String, Object> parameters) {
        VoteInstance instance = new VoteInstance(preset.identifier(), preset.name(), preset.description(), preset.voteSettings(), parameters);
        if (this.openVote(instance, participants)) {
            return Optional.of(instance);
        } else return Optional.empty();
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
        return voteInstance.closeVote(VoteSettings.CloseReason.MANUAL);
    }

    public void closeAllVotes() {
        this.forEachVotes((id, vote) -> this.closeVote(id));
    }
}
