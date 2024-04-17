package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.event.ServerVoteEvents;
import com.github.winexp.battlegrounds.task.ScheduledTask;
import com.github.winexp.battlegrounds.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;

public class VoteInstance {
    private final Identifier identifier;
    private final UUID uuid = UUID.randomUUID();
    private final Text name;
    private final Text description;
    private final VoteSettings settings;
    private final Map<String, Object> parameters;
    private boolean voting = false;
    private ScheduledTask timeoutTask = ScheduledTask.NONE_TASK;
    private ImmutableList<UUID> participants = ImmutableList.of();
    private final Object lock = new Object();
    private final Map<UUID, Boolean> voteResultMap = new ConcurrentHashMap<>();

    public VoteInstance(Identifier identifier, VoteSettings settings, Map<String, Object> parameters) {
        this.identifier = identifier;
        this.name = Text.of(identifier);
        this.description = Text.empty();
        this.settings = settings;
        this.parameters = parameters == null ? new HashMap<>() : parameters;
    }

    public VoteInstance(Identifier identifier, Text name, VoteSettings settings, Map<String, Object> parameters) {
        this.identifier = identifier;
        this.name = name;
        this.description = Text.empty();
        this.settings = settings;
        this.parameters = parameters == null ? new HashMap<>() : parameters;
    }

    public VoteInstance(Identifier identifier, Text name, Text description, VoteSettings settings, Map<String, Object> parameters) {
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.settings = settings;
        this.parameters = parameters == null ? new HashMap<>() : parameters;
    }

    public int getTimeLeft() {
        if (this.settings.timeout() == Duration.INFINITY) {
            return -1;
        } else return this.timeoutTask.getDelayTicks();
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

    public UUID getUuid() {
        return uuid;
    }

    public VoteInfo getVoteInfo(ServerPlayerEntity player) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        return this.getVoteInfo(uuid);
    }

    public VoteInfo getVoteInfo(UUID uuid) {
        return new VoteInfo(this.identifier, this.uuid, this.name, this.description, this.getTimeLeft(), this.voting &&
                (this.settings.allowChangeVote() || !this.isVoted(uuid)));
    }

    public VoteInfo getVoteInfo() {
        return new VoteInfo(this.identifier, this.uuid, this.name, this.description, this.getTimeLeft(), this.voting);
    }

    public Optional<Object> getParameter(String key) {
        return Optional.ofNullable(this.parameters.get(key));
    }

    public VoteSettings getSettings() {
        return settings;
    }

    public Collection<UUID> getParticipants() {
        return this.voteResultMap.keySet();
    }

    public int getAcceptedNum() {
        int num = 0;
        for (boolean accepted : this.voteResultMap.values()) {
            if (accepted) num++;
        }
        return num;
    }

    public boolean isParticipant(ServerPlayerEntity player) {
        return this.isParticipant(PlayerUtil.getAuthUUID(player));
    }

    public boolean isParticipant(UUID uuid) {
        return this.participants.contains(uuid);
    }

    public boolean isVoted(ServerPlayerEntity player) {
        return this.isVoted(PlayerUtil.getAuthUUID(player));
    }

    public boolean isVoted(UUID uuid) {
        return this.voteResultMap.containsKey(uuid);
    }
    
    public boolean acceptVote(ServerPlayerEntity player) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        if (!this.voting) return false;
        if (!this.isParticipant(player)) return false;
        if (!this.settings.allowChangeVote() && this.isVoted(uuid)) return false;
        this.voteResultMap.put(uuid, true);
        this.settings.playerVotedAction().accept(this, player, true);
        ServerVoteEvents.PLAYER_VOTED.invoker().onPlayerVoted(player, this, true);
        if (this.settings.voteMode().acceptPredicate.test(this.participants.size(), this.getAcceptedNum())) {
            this.closeVote(VoteSettings.CloseReason.ACCEPTED);
        }
        return true;
    }

    public boolean denyVote(ServerPlayerEntity player) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        if (!this.voting) return false;
        if (!this.isParticipant(player)) return false;
        if (!this.settings.allowChangeVote() && this.isVoted(uuid)) return false;
        if (this.settings.voteMode().canDenyCancel) this.closeVote(VoteSettings.CloseReason.DENIED);
        this.voteResultMap.put(uuid, false);
        this.settings.playerVotedAction().accept(this, player, false);
        ServerVoteEvents.PLAYER_VOTED.invoker().onPlayerVoted(player, this, false);
        return true;
    }

    public boolean openVote(Collection<ServerPlayerEntity> participants) {
        synchronized (this.lock) {
            if (this.voting) return false;
            this.voteResultMap.clear();
            this.participants = ImmutableList.copyOf(participants.stream().map(PlayerUtil::getAuthUUID).toList());
            if (this.settings.timeout() != Duration.INFINITY) {
                this.timeoutTask = new ScheduledTask(this.settings.timeout()) {
                    @Override
                    public void run() throws CancellationException {
                        VoteInstance.this.closeVote(VoteSettings.CloseReason.TIMEOUT);
                    }
                };
                TaskScheduler.INSTANCE.schedule(this.timeoutTask);
            }
            this.voting = true;
            ServerVoteEvents.OPENED.invoker().onOpened(this);
            return true;
        }
    }

    public boolean closeVote(VoteSettings.CloseReason closeReason) {
        synchronized (this.lock) {
            if (!this.voting) return false;
            this.timeoutTask.cancel();
            this.voting = false;
            this.settings.voteClosedAction().accept(this, closeReason);
            ServerVoteEvents.CLOSED.invoker().onClosed(this, closeReason);
            return true;
        }
    }

    @Override
    public int hashCode() {
        return 31 * this.identifier.hashCode() + this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VoteInstance voteInstance)) return false;
        else if (!this.identifier.equals(voteInstance.identifier)) return false;
        else return this.uuid.equals(voteInstance.uuid);
    }
}
