package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.events.VoteEvents;
import com.github.winexp.battlegrounds.task.TaskLater;
import com.github.winexp.battlegrounds.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class VoteInstance implements AutoCloseable {
    private final Identifier identifier;
    private final Text name;
    private final Text description;
    private final VoteSettings settings;
    private boolean voting = false;
    private TaskLater timeoutTask = TaskLater.NONE_TASK;
    private final HashMap<UUID, @Nullable Boolean> voteMap = new HashMap<>();

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

    public long getTimeLeft() {
        return this.timeoutTask.getDelay();
    }

    public boolean isVoting() {
        return this.voting;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public int getTotal() {
        return this.voteMap.size();
    }

    public Collection<UUID> getParticipants() {
        return this.voteMap.keySet();
    }

    public boolean isPlayerParticipants(UUID uuid) {
        return this.voteMap.get(uuid) != null;
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
        if (!this.settings.voteMode().allowChangeVote && this.isPlayerParticipants(uuid)) return false;
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
        if (!this.settings.voteMode().allowChangeVote && this.isPlayerParticipants(uuid)) return false;
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
                this.closeVote(VoteSettings.CloseReason.TIMEOUT), settings.timeout());
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
