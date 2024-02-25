package com.github.winexp.battlegrounds.helper;

import com.github.winexp.battlegrounds.util.Variables;
import com.mojang.authlib.GameProfile;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class TeamHelper {
    private final HashMap<String, Team> teams = new HashMap<>();
    private final HashMap<GameProfile, Team> playerMap = new HashMap<>();
    private final Scoreboard scoreboard;
    private int maxPlayers = 1;

    public TeamHelper(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public Team addTeam(String teamName) {
        Team team = teams.get(teamName);
        if (team != null) return team;
        team = scoreboard.getTeam(teamName);
        if (team != null) scoreboard.removeTeam(team);
        team = scoreboard.addTeam(teamName);
        teams.put(teamName, team);
        return team;
    }

    public void removeTeam(String teamName) {
        teams.remove(teamName);
        Team team = scoreboard.getTeam(teamName);
        Objects.requireNonNull(team);
        scoreboard.removeTeam(team);
    }

    public void assignPlayers(Collection<ServerPlayerEntity> players) {
        if (this.maxPlayers * this.teams.size() > Variables.progress.players.keySet().size()) throw new RuntimeException("玩家数量不足");
        for (ServerPlayerEntity player : players) {
            if (player != null) {
                while (true) {
                    int idx = player.getRandom().nextInt(this.teams.size());
                    String teamName = this.teams.keySet().toArray(new String[0])[idx];
                    Team team = this.getTeam(teamName);
                    assert team != null;
                    if (team.getPlayerList().size() >= this.maxPlayers) continue;
                    this.addPlayerToTeam(teamName, player);
                    break;
                }
            }
        }
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Nullable
    public Team getTeam(String teamName) {
        return teams.get(teamName);
    }

    @Nullable
    public Team getPlayerTeam(ServerPlayerEntity player) {
        return playerMap.get(player.getGameProfile());
    }

    public void addPlayerToTeam(String teamName, ServerPlayerEntity player) {
        Team team = scoreboard.getTeam(teamName);
        Objects.requireNonNull(team);
        scoreboard.addScoreHolderToTeam(
                ScoreHolder.fromProfile(player.getGameProfile()).getNameForScoreboard(), team
        );
        playerMap.put(player.getGameProfile(), team);
    }

    public void removePlayerToTeam(String teamName, ServerPlayerEntity player) {
        Team team = scoreboard.getTeam(teamName);
        Objects.requireNonNull(team);
        scoreboard.removeScoreHolderFromTeam(
                ScoreHolder.fromProfile(player.getGameProfile()).getNameForScoreboard(), team
        );
        playerMap.remove(player.getGameProfile());
    }
}
