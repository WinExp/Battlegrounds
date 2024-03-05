package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.events.vote.PlayerVotedCallback;
import com.github.winexp.battlegrounds.events.vote.VoteCompletedCallback;
import com.github.winexp.battlegrounds.helper.task.Task;
import com.github.winexp.battlegrounds.helper.task.TaskLater;
import com.github.winexp.battlegrounds.helper.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.Variables;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//@Deprecated(forRemoval = true)
public class VoteHelper implements AutoCloseable {
    private final HashMap<GameProfile, Boolean> voteMap = new HashMap<>();
    private boolean voting = false;
    private TaskLater timeoutTask = TaskLater.NONE_TASK;
    private TaskLater cooldownTask = TaskLater.NONE_TASK;

    public VoteHelper() {
    }

    public long getCooldown() {
        return cooldownTask.getDelay();
    }

    private void ensureIsVoting() {
        if (!voting) throw new RuntimeException("此实例没有正在进行的投票");
    }

    private boolean checkVoteCompleted() {
        ensureIsVoting();
        AtomicBoolean result = new AtomicBoolean(true);
        voteMap.forEach((key, value) -> {
            if (!value && result.get()) {
                result.set(false);
            }
        });
        return result.get();
    }

    public int getTotal() {
        ensureIsVoting();
        return voteMap.size();
    }

    public int getAccepted() {
        ensureIsVoting();
        AtomicInteger result = new AtomicInteger(0);
        voteMap.forEach((key, value) -> {
            if (value) result.incrementAndGet();
        });
        return result.get();
    }

    public GameProfile[] getPlayerProfiles() {
        ensureIsVoting();
        return voteMap.keySet().toArray(new GameProfile[0]);
    }

    public boolean isVoting() {
        return voting;
    }

    public void acceptVote(ServerPlayerEntity player) {
        ensureIsVoting();
        GameProfile profile = player.getGameProfile();
        if (!voteMap.containsKey(profile)) throw new RuntimeException(player.getName() + " 不在投票队列中！");
        if (voteMap.get(profile)) throw new RuntimeException(player.getName() + " 已经投过票了！");
        voteMap.put(profile, true);
        PlayerVotedCallback.EVENT.invoker().interact(this, player, true);
        if (checkVoteCompleted()) {
            stopVote(VoteCompletedCallback.Reason.ACCEPT);
        }
    }

    public void denyVote(ServerPlayerEntity player) {
        ensureIsVoting();
        GameProfile profile = player.getGameProfile();
        if (!voteMap.containsKey(profile)) throw new RuntimeException(player.getName() + " 不在投票队列中！");
        if (voteMap.get(profile)) throw new RuntimeException(player.getName() + " 已经投过票了！");
        PlayerVotedCallback.EVENT.invoker().interact(this, player, false);
        stopVote(VoteCompletedCallback.Reason.DENY);
    }

    public void startVote(ServerPlayerEntity[] players) {
        if (cooldownTask.getDelay() > 0) throw new RuntimeException("此实例正在冷却");
        if (voting) throw new RuntimeException("此实例正在进行投票");
        voting = true;
        voteMap.clear();
        for (ServerPlayerEntity player : players) {
            voteMap.put(player.getGameProfile(), false);
        }
        timeoutTask = new TaskLater(() -> stopVote(VoteCompletedCallback.Reason.TIMEOUT),
                Variables.config.timeout.discussionTimeoutTicks);
        TaskScheduler.INSTANCE.runTask(timeoutTask);
    }

    public void stopVote(VoteCompletedCallback.Reason reason) {
        if (!voting) return;
        VoteCompletedCallback.EVENT.invoker().interact(this, reason);
        voting = false;
        timeoutTask.cancel();
        cooldownTask.cancel();
        cooldownTask = new TaskLater(Task.NONE_RUNNABLE, Variables.config.cooldown.startGameVoteCooldownTicks);
        TaskScheduler.INSTANCE.runTask(cooldownTask);
    }

    @Override
    public void close() {
        voting = false;
        timeoutTask.cancel();
        cooldownTask.cancel();
    }
}
