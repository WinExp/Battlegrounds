package com.github.winexp.battlegrounds.helper;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.helper.task.RunnableCancelledException;
import com.github.winexp.battlegrounds.helper.task.TaskTimer;
import com.github.winexp.battlegrounds.util.FileUtil;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import com.github.winexp.battlegrounds.util.TextUtil;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameHelper {
    public TaskTimer reduceTask = TaskTimer.NONE_TASK;
    public TaskTimer barUpdateTask = TaskTimer.NONE_TASK;
    private WorldHelper worldHelper;
    private CommandBossBar bossBar;
    private final static Identifier BAR_ID = new Identifier("battlegrounds", "progress_bar");

    public GameHelper(){
    }

    public static void resetWorlds(){
        Path savePath = Battlegrounds.getSavePath();
        FileUtil.delete(savePath, "bg_progress.json");
    }

    public static void setInitialProgress(){
        Battlegrounds.progress.pvpMode = GameProgress.PVPMode.PEACEFUL;
        Battlegrounds.progress.currentLap = 0;
        Battlegrounds.progress.resizeLapTimer = Battlegrounds.config.border.resizeDelayTicks;
    }

    public static void setKeepInventory(boolean value){
        Battlegrounds.server.getGameRules().get(GameRules.KEEP_INVENTORY).set(value, Battlegrounds.server);
    }

    public static int getInGamePlayers(){
        int num = 0;
        for (ServerPlayerEntity player : Battlegrounds.server.getPlayerManager().getPlayerList()){
            if (player.interactionManager.getGameMode() == GameMode.SURVIVAL && Battlegrounds.progress.players.contains(player.getGameProfile().getId().toString())) num++;
        }
        return num;
    }

    public static ServerPlayerEntity getFirstPlayer(){
        return Battlegrounds.server.getPlayerManager().getPlayerList().get(0);
    }

    public static void stopServer(Text message){
        CopyOnWriteArrayList<ServerPlayerEntity> players = new CopyOnWriteArrayList<>(Battlegrounds.server.getPlayerManager().getPlayerList());
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

        for (String uuid : Battlegrounds.progress.players){
            ServerPlayerEntity player = Battlegrounds.server.getPlayerManager().getPlayer(UUID.fromString(uuid));
            if (player != null) {
                PlayerUtil.randomTeleport(Battlegrounds.server.getOverworld(), player);
            }
        }
        Battlegrounds.progress.pvpMode = GameProgress.PVPMode.NO_PVP;
        runTasks();
        createBossBar();
    }

    public void resumeGame(){
        World world = Battlegrounds.server.getOverworld();
        worldHelper = new WorldHelper(world);
        runTasks();
        createBossBar();
    }

    private void createBossBar(){
        BossBarManager manager = Battlegrounds.server.getBossBarManager();
        if (manager.get(BAR_ID) != null){
            bossBar = manager.get(BAR_ID);
        }
        else{
            bossBar = manager.add(BAR_ID,
                    TextUtil.translatableWithColor("battlegrounds.border.bar",
                            TextUtil.GREEN,
                            reduceTask.getDelay() / 20)
            );
        }
        barUpdateTask = new TaskTimer(() -> {
            if (bossBar == null){
                throw new RunnableCancelledException();
            }
            bossBar.addPlayers(Battlegrounds.server.getPlayerManager().getPlayerList());
            bossBar.setMaxValue((int) (Battlegrounds.config.border.resizeDelayTicks + Battlegrounds.config.border.resizeTimeTicks));
            bossBar.setValue((int) reduceTask.getDelay());
            bossBar.setName(TextUtil.translatableWithColor("battlegrounds.border.bar",
                    TextUtil.GREEN,
                    reduceTask.getDelay() / 20));
        }, reduceTask.getDelay() % 20, 20);
        Battlegrounds.taskScheduler.runTask(barUpdateTask);
    }

    private void removeBossBar(){
        BossBarManager manager = Battlegrounds.server.getBossBarManager();
        CommandBossBar bar = manager.get(BAR_ID);
        if (bar != null){
            bar.clearPlayers();
            manager.remove(bar);
        }
        bossBar = null;
    }

    public void runTasks(){
        reduceTask = new TaskTimer(() -> {
            // 启用 PVP
            if (Battlegrounds.progress.currentLap + 1
                    == Battlegrounds.config.pvpModeBeginBorderNum){
                Battlegrounds.progress.pvpMode = GameProgress.PVPMode.PVP_MODE;
                Battlegrounds.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.pvp.enable.broadcast", TextUtil.GOLD), false);
            }
            // 最终圈
            if (Battlegrounds.progress.currentLap + 1
                    == Battlegrounds.config.border.resizeNum){
                Battlegrounds.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.deathmatch.already.broadcast", TextUtil.GOLD,
                        (Battlegrounds.config.border.resizeDelayTicks + Battlegrounds.config.border.resizeTimeTicks) / 1200), false);
            }
            // 死亡竞赛
            else if (Battlegrounds.progress.currentLap
                    >= Battlegrounds.config.border.resizeNum){
                setKeepInventory(false);
                Battlegrounds.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.deathmatch.start.broadcast", TextUtil.GOLD), false);
                worldHelper.setBorderSize(Battlegrounds.config.border.finalSize);
                if (Battlegrounds.progress.progress != GameProgress.Progress.DEATHMATCH){
                    for (String uuid : Battlegrounds.progress.players){
                        ServerPlayerEntity player = Battlegrounds.server.getPlayerManager().getPlayer(UUID.fromString(uuid));
                        if (player != null) {
                            PlayerUtil.randomTeleport(Battlegrounds.server.getOverworld(), player);
                        }
                    }
                }
                removeBossBar();
                Battlegrounds.progress.progress = GameProgress.Progress.DEATHMATCH;

                throw new RunnableCancelledException();
            }
            for (ServerPlayerEntity player : Battlegrounds.server.getPlayerManager().getPlayerList()){
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1);
            }
            worldHelper.setBorderSize(worldHelper.getBorderSize() - Battlegrounds.config.border.resizeBlocks,
                    Battlegrounds.config.border.resizeTimeTicks * 50);
            Battlegrounds.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.game.border.reduce.broadcast", TextUtil.GOLD), false);
            Battlegrounds.progress.currentLap++;
        }, Battlegrounds.progress.resizeLapTimer,
                () -> Battlegrounds.config.border.resizeDelayTicks + Battlegrounds.config.border.resizeTimeTicks);
        Battlegrounds.taskScheduler.runTask(reduceTask);
    }

    public void stopGame(){
        Battlegrounds.progress.progress = GameProgress.Progress.IDLE;
        GameHelper.setInitialProgress();
        worldHelper.setBorderSize(Battlegrounds.config.border.initialSize);
    }
}
