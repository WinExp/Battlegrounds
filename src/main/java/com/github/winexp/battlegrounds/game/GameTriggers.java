package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.registry.ModRegistries;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class GameTriggers {
    public static final GameTrigger NONE = register("none", gameManager -> {});
    public static final GameTrigger DEVELOP_BEGIN = register("develop_begin", gameManager -> {});
    public static final GameTrigger ENABLE_PVP = register("enable_pvp", gameManager -> {
        gameManager.setPVPMode(PVPMode.PVP_MODE);
        for (ServerPlayerEntity player : gameManager.getServer().getPlayerManager().getPlayerList()) {
            UUID uuid = PlayerUtil.getAuthUUID(player);
            PlayerPermission permission = gameManager.getPlayerPermission(uuid, new PlayerPermission());
            if (!permission.isDead) {
                permission.hasEnrichEffects = false;
            }
        }
        gameManager.getServer().getPlayerManager().broadcast(
                Text.translatable("game.battlegrounds.pvp.enable.broadcast")
                        .formatted(Formatting.GOLD)
                        .styled(style -> style.withUnderline(true)),
                false
        );
    });

    public static final GameTrigger DEATHMATCH_BEGIN = register("deathmatch_begin", gameManager -> {
        gameManager.getServer().getPlayerManager().broadcast(
                Text.translatable("game.battlegrounds.deathmatch.start.broadcast")
                        .formatted(Formatting.GOLD)
                        .styled(style -> style.withBold(true).withUnderline(true)),
                false
        );
    });

    public static final GameTrigger FINAL_BEGIN = register("final_begin", gameManager -> {
        gameManager.setGameStage(GameStage.FINAL);
        gameManager.enableTimeoutTimer(gameManager.getGameProperties().timeout().toTicks());
    });

    private static GameTrigger register(String id, GameTrigger gameTrigger) {
        return Registry.register(ModRegistries.GAME_TRIGGER, new Identifier("battlegrounds", id), gameTrigger);
    }

    public static void initialize() {
    }
}
