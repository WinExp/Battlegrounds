package com.github.winexp.battlegrounds.helper;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.helper.task.RunnableCancelledException;
import com.github.winexp.battlegrounds.helper.task.TaskTimer;
import com.github.winexp.battlegrounds.util.FileUtil;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import com.github.winexp.battlegrounds.util.TextUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameHelper {
    public TaskTimer reduceTask = TaskTimer.NONE_TASK;
    private WorldHelper worldHelper;

    public static void resetWorlds(){
        Path savePath = Path.of(Battlegrounds.cache.savePath);
        FileUtil.delete(savePath, "bg_progress.json");
    }

    public static void setInitialProgress(){
        Battlegrounds.progress.gaming = true;
        Battlegrounds.progress.pvpMode = GameProgress.PVPMode.PEACEFUL;
        Battlegrounds.progress.currentLap = 0;
        Battlegrounds.progress.resizeLapTimer = Battlegrounds.config.border.resizeDelayTicks;
        Battlegrounds.progress.deathmatch = false;
    }

    public static void setKeepInventory(boolean value){
        Battlegrounds.server.getGameRules().get(GameRules.KEEP_INVENTORY).set(value, Battlegrounds.server);
    }

    public static int getInGamePlayers(){
        int num = 0;
        for (ServerPlayerEntity player : Battlegrounds.server.getPlayerManager().getPlayerList()){
            if (Battlegrounds.progress.players.contains(player.getGameProfile().getId().toString())) num++;
        }
        return num;
    }

    public static ServerPlayerEntity getFirstPlayer(){
        return Battlegrounds.server.getPlayerManager().getPlayerList().get(0);
    }

    public static void stopServer(Text message){
        CopyOnWriteArrayList<ServerPlayerEntity> players = (CopyOnWriteArrayList<ServerPlayerEntity>) Battlegrounds.server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity player : players){
            player.networkHandler.disconnect(message);
        }
        Battlegrounds.server.stop(false);
    }

    public void startGame(){
        World world = Battlegrounds.server.getOverworld();
        worldHelper = new WorldHelper(world);
        BlockPos pos = RandomUtil.getSecureLocation(world);
        worldHelper.setBorderCenter(pos.getX(), pos.getZ());
        worldHelper.setBorderSize(Battlegrounds.config.border.initialSize);

        for (ServerPlayerEntity player : Battlegrounds.server.getPlayerManager().getPlayerList()){
            PlayerUtil.randomTeleport(world, player);
        }
        Battlegrounds.progress.pvpMode = GameProgress.PVPMode.NO_PVP;

        reduceTask = new TaskTimer(() -> {
            if (Battlegrounds.progress.currentLap + 1
            == Battlegrounds.config.pvpModeBeginBorderNum){
                Battlegrounds.progress.pvpMode = GameProgress.PVPMode.PVP_MODE;
                Battlegrounds.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.pvp.enable.broadcast", TextUtil.GOLD), false);
            }

            if (Battlegrounds.progress.currentLap + 1
            == Battlegrounds.config.border.resizeNum){
                Battlegrounds.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.deathmatch.already.broadcast", TextUtil.GOLD,
                        Battlegrounds.config.border.resizeDelayTicks / 1200), false);
            }
            else if (Battlegrounds.progress.currentLap
                    >= Battlegrounds.config.border.resizeNum){
                Battlegrounds.progress.deathmatch = true;
                setKeepInventory(false);
                Battlegrounds.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.deathmatch.start.broadcast", TextUtil.GOLD), false);
                worldHelper.setBorderSize(Battlegrounds.config.border.finalSize);
                for (ServerPlayerEntity player : Battlegrounds.server.getPlayerManager().getPlayerList()){
                    PlayerUtil.randomTeleport(Battlegrounds.server.getOverworld(), player);
                }
                throw new RunnableCancelledException();
            }
            worldHelper.setBorderSize(worldHelper.getBorderSize() - Battlegrounds.config.border.resizeBlocks,
                    Battlegrounds.config.border.resizeTimeTicks * 50);
            Battlegrounds.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.game.border.reduce.broadcast", TextUtil.GOLD), false);
            Battlegrounds.progress.currentLap++;
        }, Battlegrounds.progress.resizeLapTimer,
                Battlegrounds.progress.resizeLapTimer + Battlegrounds.config.border.resizeTimeTicks);
        Battlegrounds.taskScheduler.runTask(reduceTask);
    }

    public void stopGame(){
        setInitialProgress();
        Battlegrounds.progress.gaming = false;
        worldHelper.setBorderSize(Battlegrounds.config.border.initialSize);
    }
}
