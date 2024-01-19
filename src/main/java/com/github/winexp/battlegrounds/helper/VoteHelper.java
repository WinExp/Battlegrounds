package com.github.winexp.battlegrounds.helper;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.events.vote.PlayerVotedCallback;
import com.github.winexp.battlegrounds.events.vote.VoteCompletedCallback;
import com.github.winexp.battlegrounds.helper.task.Task;
import com.github.winexp.battlegrounds.helper.task.TaskLater;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class VoteHelper{
    private boolean voting = false;
    private final HashMap<GameProfile, Boolean> voteMap = new HashMap<>();
    private TaskLater timeoutTask = TaskLater.NONE_TASK;
    private TaskLater cooldownTask = TaskLater.NONE_TASK;

    public VoteHelper() { }

    public long getCooldown() { return cooldownTask.getDelay(); }

    private boolean checkVoteCompleted(){
        if (!voting) throw new RuntimeException("此实例没有正在进行的投票");
        AtomicBoolean result = new AtomicBoolean(true);
        voteMap.forEach((key, value) -> {
            if (!value){
                result.set(false);
            }
        });
        return result.get();
    }

    public int getTotal(){
        if (!voting) throw new RuntimeException("此实例没有正在进行的投票");
        return voteMap.size();
    }

    public int getAccepted(){
        if (!voting) throw new RuntimeException("此实例没有正在进行的投票");
        AtomicInteger result = new AtomicInteger(0);
        voteMap.forEach((key, value) -> {
            if (value) result.incrementAndGet();
        });
        return result.get();
    }

    public GameProfile[] getProfiles(){
        if (!voting) throw new RuntimeException("此实例没有正在进行的投票");
        return voteMap.keySet().toArray(new GameProfile[0]);
    }

    public boolean isVoting(){ return voting; }

    public void acceptVote(ServerPlayerEntity player){
        if (!voting) throw new RuntimeException("此实例没有正在进行的投票");
        GameProfile profile = player.getGameProfile();
        if (!voteMap.containsKey(profile)) throw new RuntimeException(player.getName() + " 不在投票队列中！");
        voteMap.put(profile, true);
        PlayerVotedCallback.EVENT.invoker().interact(player, this, true);
        if (checkVoteCompleted()){
            stopVote(VoteCompletedCallback.Reason.ACCEPT);
        }
    }

    public void denyVote(ServerPlayerEntity player){
        if (!voting) throw new RuntimeException("此实例没有正在进行的投票");
        GameProfile profile = player.getGameProfile();
        if (!voteMap.containsKey(profile)) throw new RuntimeException(player.getName() + " 不在投票队列中！");
        PlayerVotedCallback.EVENT.invoker().interact(player, this, false);
        stopVote(VoteCompletedCallback.Reason.DENY);
    }

    public void startVote(ServerPlayerEntity[] players){
        if (cooldownTask.getDelay() > 0) throw new RuntimeException("此实例正在冷却");
        if (voting) throw new RuntimeException("此实例正在进行投票");
        voting = true;
        voteMap.clear();
        for (ServerPlayerEntity player : players){
            voteMap.put(player.getGameProfile(), false);
        }
        timeoutTask = new TaskLater(() -> stopVote(VoteCompletedCallback.Reason.TIMEOUT),
                Battlegrounds.config.voteTimeoutTicks);
        Battlegrounds.taskScheduler.runTask(timeoutTask);
    }

    public void stopVote(VoteCompletedCallback.Reason reason){
        if (!voting) return;
        VoteCompletedCallback.EVENT.invoker().interact(this, reason);
        voting = false;
        timeoutTask.cancel();
        cooldownTask.cancel();
        cooldownTask = new TaskLater(Task.NONE_RUNNABLE, Battlegrounds.config.voteCooldownTicks);
        Battlegrounds.taskScheduler.runTask(cooldownTask);
    }
}
