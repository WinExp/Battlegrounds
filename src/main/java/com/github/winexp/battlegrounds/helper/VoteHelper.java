package com.github.winexp.battlegrounds.helper;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.events.server.ServerTickCallback;
import com.github.winexp.battlegrounds.events.vote.PlayerVotedCallback;
import com.github.winexp.battlegrounds.events.vote.VoteCompletedCallback;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class VoteHelper{
    private boolean voting = false;
    private HashMap<GameProfile, Boolean> voteMap = new HashMap<>();
    private long timeout;
    private long cooldown;

    public VoteHelper(){
        ServerTickCallback.EVENT.register(this::onTick);
    }

    public long getCooldown() {
        return cooldown;
    }

    private ActionResult onTick(MinecraftServer server){
        if (timeout > 0) timeout--;
        if (cooldown > 0) cooldown--;
        if (voting && timeout <= 0){
            stopVote(VoteCompletedCallback.Reason.TIMEOUT);
        }

        return ActionResult.PASS;
    }

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
        if (cooldown > 0) throw new RuntimeException("此实例正在冷却");
        if (voting) throw new RuntimeException("此实例正在进行投票");
        voting = true;
        voteMap.clear();
        for (ServerPlayerEntity player : players){
            voteMap.put(player.getGameProfile(), false);
        }
        timeout = Battlegrounds.config.voteTimeoutTicks;
        cooldown = Battlegrounds.config.voteCooldownTicks;
    }

    public void stopVote(VoteCompletedCallback.Reason reason){
        if (!voting) throw new RuntimeException("此实例没有正在进行的投票");
        VoteCompletedCallback.EVENT.invoker().interact(this, reason);
        voting = false;
        cooldown = Battlegrounds.config.voteCooldownTicks;
    }
}
