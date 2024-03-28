package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.event.ServerVoteEvents;
import com.github.winexp.battlegrounds.task.ScheduledTask;
import com.github.winexp.battlegrounds.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;

public class VoteInstance {
    private final Identifier identifier;
    private final UUID uuid = UUID.randomUUID();
    private final Text name;
    private final Text description;
    private final VoteSettings settings;
    private boolean voting = false;
    private ScheduledTask timeoutTask = ScheduledTask.NONE_TASK;
    private ImmutableList<UUID> participants = ImmutableList.of();
    private final ConcurrentHashMap<UUID, Boolean> voteResultMap = new ConcurrentHashMap<>();

    public VoteInstance(Identifier identifier, VoteSettings settings) {
        this.identifier = identifier;
        this.name = Text.of(identifier);
        this.description = Text.empty();
        this.settings = settings;
    }

    public VoteInstance(Identifier identifier, Text name, VoteSettings settings) {
        this.identifier = identifier;
        this.name = name;
        this.description = Text.empty();
        this.settings = settings;
    }

    public VoteInstance(Identifier identifier, Text name, Text description, VoteSettings settings) {
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.settings = settings;
    }

    public UUID getUuid() {
        return uuid;
    }

    public VoteInfo getVoteInfo() {
        return new VoteInfo(this.identifier, this.uuid, this.name, this.description, this.getTimeLeft());
    }

    public long getTimeLeft() {
        if (this.settings.timeout() == VoteSettings.INFINITE_TIME) {
            return -1;
        } else return this.timeoutTask.getDelay();
    }

    public boolean isVoting() {
        return this.voting;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public Text getName() {
        return this.name;
    }

    public Text getDescription() {
        return this.description;
    }

    public int getTotal() {
        return this.voteResultMap.size();
    }

    public Collection<UUID> getParticipants() {
        return this.voteResultMap.keySet();
    }

    public boolean isParticipants(ServerPlayerEntity player) {
        return this.participants.contains(PlayerUtil.getAuthUUID(player));
    }

    public boolean isParticipants(UUID uuid) {
        return this.participants.contains(uuid);
    }

    public int getAcceptedNum() {
        int num = 0;
        for (boolean accepted : this.voteResultMap.values()) {
            if (accepted) num++;
        }
        return num;
    }
    
    public boolean acceptVote(ServerPlayerEntity player) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        if (!this.voting) return false;
        if (!this.isParticipants(player)) return false;
        if (!this.settings.voteMode().allowChangeVote && this.voteResultMap.containsKey(uuid)) return false;
        this.voteResultMap.put(uuid, true);
        this.settings.playerVotedAction().accept(this, player, true);
        if (this.settings.voteMode().acceptPredicate.test(this.participants.size(), this.getAcceptedNum())) {
            this.closeVote(VoteSettings.CloseReason.ACCEPTED);
        }
        return true;
    }

    public boolean denyVote(ServerPlayerEntity player) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        if (!this.voting) return false;
        if (!this.isParticipants(player)) return false;
        if (!this.settings.voteMode().allowChangeVote && this.voteResultMap.containsKey(uuid)) return false;
        if (this.settings.voteMode().canDenyCancel) this.closeVote(VoteSettings.CloseReason.DENIED);
        this.voteResultMap.put(uuid, false);
        this.settings.playerVotedAction().accept(this, player, false);
        return true;
    }

    public boolean openVote(Collection<ServerPlayerEntity> participants) {
        if (this.voting) return false;
        this.voteResultMap.clear();
        ImmutableList.Builder<UUID> listBuilder = ImmutableList.builder();
        participants.forEach(player -> {
            listBuilder.add(PlayerUtil.getAuthUUID(player));
        });
        this.participants = listBuilder.build();
        if (this.settings.timeout() != VoteSettings.INFINITE_TIME) {
            this.timeoutTask = new ScheduledTask(this.settings.timeout()) {
                @Override
                public void run() throws CancellationException {
                    VoteInstance.this.closeVote(VoteSettings.CloseReason.TIMEOUT);
                }
            };
            TaskScheduler.INSTANCE.schedule(this.timeoutTask);
        }
        ServerVoteEvents.OPENED.invoker().onOpened(this.getVoteInfo());
        this.voting = true;
        return true;
    }

    public boolean closeVote(VoteSettings.CloseReason closeReason) {
        if (!this.voting) return false;
        this.timeoutTask.cancel();
        this.voting = false;
        this.settings.voteClosedAction().accept(this, closeReason);
        ServerVoteEvents.CLOSED.invoker().onClosed(this.getVoteInfo(), closeReason);
        return true;
    }

    @Override
    public int hashCode() {
        return 12 * this.identifier.hashCode() + 42 * this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VoteInstance voteInstance)) return false;
        else if (!this.identifier.equals(voteInstance.identifier)) return false;
        else return this.uuid.equals(voteInstance.uuid);
    }
}
