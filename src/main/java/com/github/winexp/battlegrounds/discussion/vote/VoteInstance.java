package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.events.VoteEvents;
import com.github.winexp.battlegrounds.helper.task.TaskLater;
import com.github.winexp.battlegrounds.helper.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class VoteInstance implements AutoCloseable {
    private final Identifier identifier;
    private final String name;
    private final String description;
    private final VoteSettings settings;
    private boolean voting = false;
    private TaskLater timeoutTask = TaskLater.NONE_TASK;
    private final HashMap<UUID, @Nullable Boolean> voteMap = new HashMap<>();

    public VoteInstance(Identifier identifier, VoteSettings settings) {
        this.identifier = identifier;
        this.name = identifier.toString();
        this.description = "";
        this.settings = settings;
    }

    public VoteInstance(Identifier identifier, String name, VoteSettings settings) {
        this.identifier = identifier;
        this.name = name;
        this.description = "";
        this.settings = settings;
    }

    public VoteInstance(Identifier identifier, String name, String description, VoteSettings settings) {
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.settings = settings;
    }

    public boolean isVoting() {
        return this.voting;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTotal() {
        return this.voteMap.size();
    }

    public Collection<UUID> getParticipants() {
        return this.voteMap.keySet();
    }

    public int getAcceptedNum() {
        int num = 0;
        for (boolean accepted : this.voteMap.values()) {
            if (accepted) num++;
        }
        return num;
    }

    public boolean acceptVote(ServerPlayerEntity player) {
        UUID uuid = PlayerUtil.getUUID(player);
        if (!this.voting) return false;
        if (!this.settings.voteMode().allowChangeVote && this.voteMap.containsKey(uuid)) return false;
        this.voteMap.put(uuid, true);
        this.settings.playerVotedAction().accept(this, player, true);
        if (this.settings.voteMode().acceptPredicate.test(this.voteMap.size(), this.getAcceptedNum())) {
            this.closeVote(VoteSettings.CloseReason.ACCEPTED);
        }
        return true;
    }

    public boolean denyVote(ServerPlayerEntity player) {
        UUID uuid = PlayerUtil.getUUID(player);
        if (!this.voting) return false;
        if (this.settings.voteMode().canDenyCancel) this.closeVote(VoteSettings.CloseReason.DENIED);
        if (!this.settings.voteMode().allowChangeVote && this.voteMap.containsKey(uuid)) return false;
        this.voteMap.put(uuid, false);
        this.settings.playerVotedAction().accept(this, player, false);
        return true;
    }

    public boolean openVote(Collection<ServerPlayerEntity> participants) {
        if (this.voting) return false;
        this.voteMap.clear();
        for (ServerPlayerEntity player : participants) {
            voteMap.put(PlayerUtil.getUUID(player), null);
        }
        this.timeoutTask = new TaskLater(() ->
                settings.voteClosedAction().accept(this, VoteSettings.CloseReason.TIMEOUT), settings.timeout());
        TaskScheduler.INSTANCE.runTask(this.timeoutTask);
        this.voting = true;
        return true;
    }

    public boolean closeVote(VoteSettings.CloseReason closeReason) {
        if (!this.voting) return false;
        this.timeoutTask.cancel();
        this.voting = false;
        this.settings.voteClosedAction().accept(this, closeReason);
        VoteEvents.CLOSED.invoker().interact(this, closeReason);
        return true;
    }

    @Override
    public void close() {
        this.voteMap.clear();
        this.timeoutTask.cancel();
    }
}
