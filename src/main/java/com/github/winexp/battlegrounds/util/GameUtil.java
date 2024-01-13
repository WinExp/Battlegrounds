package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.configs.GameProgress;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.nio.file.Path;

public class GameUtil {
    public static void sendTitle(ServerPlayerEntity player, Text title){
        sendTitle(player, title, Text.of(""));
    }

    public static void sendTitle(ServerPlayerEntity player, Text title, Text subtitle){
        player.networkHandler.sendPacket(new TitleS2CPacket(title));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(subtitle));
    }

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

    public static void stopServer(Text message){
        for (ServerPlayerEntity player : Battlegrounds.server.getPlayerManager().getPlayerList()){
            player.networkHandler.disconnect(message);
        }
        Battlegrounds.server.stop(false);
    }
}
