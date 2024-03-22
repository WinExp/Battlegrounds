package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.event.ServerVoteEvents;
import com.github.winexp.battlegrounds.network.packet.s2c.SyncVoteInfosS2CPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.VoteClosedPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.VoteOpenedPacket;
import com.github.winexp.battlegrounds.util.Variables;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class VoteManager {
    public static final VoteManager INSTANCE = new VoteManager();

    private final ConcurrentHashMap<Identifier, VoteInstance> voteMap = new ConcurrentHashMap<>();

    protected VoteManager() {
        ServerVoteEvents.CLOSED.register(this::onVoteClosed);
    }

    private void onVoteClosed(VoteInfo voteInfo, VoteSettings.CloseReason reason) {
        Identifier identifier = voteInfo.identifier;
        this.voteMap.remove(identifier);
        for (ServerPlayerEntity player : Variables.server.getPlayerManager().getPlayerList()) {
            VoteClosedPacket packet = new VoteClosedPacket(voteInfo, reason);
            ServerPlayNetworking.send(player, packet);
        }
    }

    public void syncVoteInfos(ServerPlayerEntity player) {
        Collection<VoteInfo> voteInfos = this.getVoteInfoList();
        SyncVoteInfosS2CPacket packet = new SyncVoteInfosS2CPacket(voteInfos);
        ServerPlayNetworking.send(player, packet);
    }

    public boolean containsVote(Identifier identifier) {
        return this.voteMap.containsKey(identifier);
    }

    public boolean isVoting(Identifier identifier) {
        return this.containsVote(identifier) && this.voteMap.get(identifier).isVoting();
    }

    public List<VoteInfo> getVoteInfoList() {
        List<VoteInfo> voteInfos = new ArrayList<>();
        this.forEachVotes((key, value) -> voteInfos.add(value.getVoteInfo()));
        return voteInfos;
    }

    public Optional<VoteInstance> getVoteInstance(Identifier identifier) {
        return Optional.ofNullable(this.voteMap.get(identifier));
    }

    public void forEachVotes(BiConsumer<Identifier, VoteInstance> consumer) {
        this.voteMap.forEach(consumer);
    }


    public Optional<VoteInstance> openVoteWithPreset(VotePreset preset, Collection<ServerPlayerEntity> participants) {
        VoteInstance instance = new VoteInstance(preset.identifier(), preset.name(), preset.description(), preset.voteSettings());
        if (this.openVote(instance, participants)) {
            return Optional.of(instance);
        } else return Optional.empty();
    }

    public boolean openVote(VoteInstance voteInstance, Collection<ServerPlayerEntity> participants) {
        Identifier identifier = voteInstance.getIdentifier();
        if (this.isVoting(identifier)) return false;
        if (!voteInstance.openVote(participants)) return false;
        this.voteMap.put(identifier, voteInstance);
        for (ServerPlayerEntity player : Variables.server.getPlayerManager().getPlayerList()) {
            VoteOpenedPacket packet = new VoteOpenedPacket(voteInstance.getVoteInfo());
            ServerPlayNetworking.send(player, packet);
        }
        return true;
    }

    public boolean closeVote(Identifier identifier) {
        if (!this.isVoting(identifier)) return false;
        VoteInstance voteInstance = this.voteMap.get(identifier);
        return voteInstance.closeVote(VoteSettings.CloseReason.MANUAL);
    }
}
