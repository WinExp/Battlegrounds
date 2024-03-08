package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.events.VoteEvents;
import com.github.winexp.battlegrounds.network.packet.s2c.SyncVoteInfoS2CPacket;
import com.github.winexp.battlegrounds.util.Variables;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class VoteManager {
    public static final VoteManager INSTANCE = new VoteManager();

    private final HashMap<Identifier, @NotNull VoteInstance> voteMap = new HashMap<>();

    public VoteManager() {
        VoteEvents.CLOSED.register(this::onVoteClosed);
    }

    private void onVoteClosed(VoteInstance voteInstance, VoteSettings.CloseReason reason) {
        try (voteInstance) {
            Identifier identifier = voteInstance.getIdentifier();
            this.voteMap.remove(identifier);
            this.updateVoteInfosToAllPlayers();
        }
    }

    public void updateVoteInfosToAllPlayers() {
        for (ServerPlayerEntity player : Variables.server.getPlayerManager().getPlayerList()) {
            this.updateVoteInfos(player, true);
        }
    }

    public void updateVoteInfos(ServerPlayerEntity player, boolean changed) {
        Collection<VoteInfo> voteInfos = this.getVoteInfos();
        SyncVoteInfoS2CPacket packet = new SyncVoteInfoS2CPacket(changed, voteInfos);
        ServerPlayNetworking.send(player, packet);
    }

    public boolean containsVote(Identifier identifier) {
        return this.voteMap.containsKey(identifier);
    }

    public boolean isVoting(Identifier identifier) {
        return this.containsVote(identifier) && this.voteMap.get(identifier).isVoting();
    }

    public Collection<VoteInfo> getVoteInfos() {
        List<VoteInfo> voteInfos = new ArrayList<>();
        this.forEach((key, value) -> {
            Text name = value.getName();
            Text description = value.getDescription();
            VoteInfo voteInfo = new VoteInfo(key, name, description, value.getTimeLeft());
            voteInfos.add(voteInfo);
        });
        return voteInfos;
    }

    @Nullable
    public VoteInstance getVoteInstance(Identifier identifier) {
        return this.voteMap.get(identifier);
    }

    public void forEach(BiConsumer<Identifier, VoteInstance> consumer) {
        this.voteMap.forEach(consumer);
    }

    public boolean openVoteWithPreset(VotePresets.Preset preset, Collection<ServerPlayerEntity> participants) {
        VoteInstance instance = new VoteInstance(preset.identifier(), preset.name(), preset.description(), preset.voteSettings());
        return this.openVote(instance, participants);
    }

    public boolean openVote(VoteInstance voteInstance, Collection<ServerPlayerEntity> participants) {
        Identifier identifier = voteInstance.getIdentifier();
        if (this.isVoting(identifier)) return false;
        if (!voteInstance.openVote(participants)) return false;
        this.voteMap.put(identifier, voteInstance);
        this.updateVoteInfosToAllPlayers();
        return true;
    }

    public boolean closeVote(Identifier identifier) {
        if (!this.isVoting(identifier)) return false;
        VoteInstance voteInstance = this.voteMap.get(identifier);
        return voteInstance.closeVote(VoteSettings.CloseReason.MANUAL);
    }
}
