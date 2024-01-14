package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.helper.WorldHelper;
import com.github.winexp.battlegrounds.helper.task.RunnableCancelledException;
import com.github.winexp.battlegrounds.helper.task.TaskTimer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.nio.file.Path;

public class GameUtil {
    private static WorldHelper worldHelper;

    public static void resetWorlds(){
        Path savePath = Path.of(Battlegrounds.cache.savePath);
        FileUtil.delete(savePath, "bg_progress.json");
    }

    public static void setInitialProgress(){
        Battlegrounds.progress.gaming = true;
        Battlegrounds.progress.pvpMode = GameProgress.PVPMode.PEACEFUL;
        Battlegrounds.progress.currentLap = 0;
        Battlegrounds.progress.resizeLapTimer = 0;
    }

    public static int getInGamePlayers(){
        int num = 0;
        for (ServerPlayerEntity player : Battlegrounds.server.getPlayerManager().getPlayerList()){
            if (Battlegrounds.progress.players.contains(player.getGameProfile().getId().toString())) num++;
        }
        return num;
    }

    public static void startGame(){
        World world = Battlegrounds.server.getOverworld();
        worldHelper = new WorldHelper(world);
        BlockPos pos = RandomUtil.getSecureLocation(world);
        worldHelper.setBorderCenter(pos.getX(), pos.getZ());
        worldHelper.setBorderSize(Battlegrounds.config.border.initialBorderSize);

        for (ServerPlayerEntity player : Battlegrounds.server.getPlayerManager().getPlayerList()){
            PlayerUtil.randomTeleport(world, player);
        }
    }

    public static void stopServer(Text message){
        for (ServerPlayerEntity player : Battlegrounds.server.getPlayerManager().getPlayerList()){
            player.networkHandler.disconnect(message);
        }
        Battlegrounds.server.stop(false);
    }
}
