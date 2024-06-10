package com.github.winexp.battlegrounds.command;

import com.github.winexp.battlegrounds.event.ModServerPlayerEvents;
import com.github.winexp.battlegrounds.game.GameListener;
import com.github.winexp.battlegrounds.game.GameManager;
import com.github.winexp.battlegrounds.game.GameTrigger;
import com.github.winexp.battlegrounds.game.GameTriggers;
import com.github.winexp.battlegrounds.util.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.task.RepeatTask;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.Variables;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;

public class RandomTpCommand {
    private static final Map<UUID, Integer> cooldownTimers = new HashMap<>();
    private static RepeatTask coolDownUpdateTask = RepeatTask.NONE_TASK;
    private static GameTrigger cooldownTrigger;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cRoot = CommandManager.literal("randomtp").requires(ServerCommandSource::isExecutedByPlayer)
                .executes(RandomTpCommand::randomtp);

        var cNode = dispatcher.register(cRoot);

        var cRoot_redir = CommandManager.literal("rtp").requires(ServerCommandSource::isExecutedByPlayer)
                .executes(RandomTpCommand::randomtp).redirect(cNode);

        dispatcher.register(cRoot_redir);

        ModServerPlayerEvents.AFTER_PLAYER_DAMAGED.register(RandomTpCommand::onPlayerDamaged);
        GameManager.addGlobalListener(new GameListener() {
            @Override
            public void onTriggered(GameManager manager, GameTrigger gameTrigger) {
                if (gameTrigger == GameTriggers.NONE) {
                    cooldownTrigger = GameTriggers.NONE;
                } else if (Variables.config.randomTp().cooldowns().get(gameTrigger) != null) {
                    cooldownTrigger = gameTrigger;
                }
            }
            @Override
            public void onBorderResizing(GameManager manager) { }
            @Override
            public void onBorderResized(GameManager manager) { }
            @Override
            public void onPlayerWin(GameManager manager, ServerPlayerEntity player) { }
            @Override
            public void onTimeout(GameManager manager) { }
        });
    }

    private static void onPlayerDamaged(ServerPlayerEntity player, DamageSource source, float amount) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        if (Variables.gameManager.getGameStage().isGaming()
                && source.getSource() != null
                && source.getSource().isPlayer()
                && Variables.gameManager.isParticipant(uuid)) {
            RandomTpCommand.updateCooldown(uuid, Variables.config.randomTp().damagedCooldown().toTicks());
        }
    }

    public static int getConfigCooldown() {
        int cooldown = Variables.config.randomTp().defaultCooldown().toTicks();
        if (cooldownTrigger != null
                && Variables.config.randomTp().cooldowns().get(cooldownTrigger) != null) {
            cooldown = Variables.config.randomTp().cooldowns().get(cooldownTrigger).toTicks();
        }
        return cooldown;
    }

    public static void updateCooldown(UUID uuid, int cooldown) {
        if (cooldownTimers.get(uuid) == null || cooldown > cooldownTimers.get(uuid)) {
            setCooldown(uuid, cooldown);
        }
    }

    public static void setCooldown(UUID uuid, int cooldown) {
        Integer currentCooldown;
        if ((currentCooldown = cooldownTimers.putIfAbsent(uuid, cooldown)) != null) {
            cooldownTimers.put(uuid, Math.max(currentCooldown, cooldown));
        }
    }

    private static int randomtp(CommandContext<ServerCommandSource> context) {
        if (coolDownUpdateTask == RepeatTask.NONE_TASK) {
            coolDownUpdateTask = new RepeatTask(Duration.ZERO, Duration.withTicks(1)) {
                @Override
                public void onTriggered() throws CancellationException {
                    cooldownTimers.replaceAll((uuid, cooldown) -> {
                        if (cooldown > 0) return cooldown - 1;
                        else return 0;
                    });
                }
            };
        }
        TaskScheduler.INSTANCE.schedule(coolDownUpdateTask);

        ServerCommandSource source = context.getSource();
        assert source.getPlayer() != null;
        UUID uuid = PlayerUtil.getAuthUUID(source.getPlayer());
        if (!cooldownTimers.containsKey(uuid)) cooldownTimers.put(uuid, 0);
        Integer cooldown = cooldownTimers.get(uuid);
        if (cooldown > 0) {
            source.sendFeedback(() -> Text.translatable("commands.battlegrounds.randomtp.delay.feedback", cooldown / 20)
                    .formatted(Formatting.RED), false);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.battlegrounds.randomtp.feedback")
                    .formatted(Formatting.GOLD), false);
            PlayerUtil.randomTeleport(Variables.server.getOverworld(), Objects.requireNonNull(source.getPlayer()));
            updateCooldown(uuid, getConfigCooldown());
        }

        return 1;
    }
}
