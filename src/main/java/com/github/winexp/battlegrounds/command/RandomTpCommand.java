package com.github.winexp.battlegrounds.command;

import com.github.winexp.battlegrounds.event.ServerGameEvents;
import com.github.winexp.battlegrounds.game.GameManager;
import com.github.winexp.battlegrounds.task.TaskScheduler;
import com.github.winexp.battlegrounds.task.RepeatTask;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.Variables;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;

public class RandomTpCommand {
    private static final HashMap<UUID, Integer> cooldownTimers = new HashMap<>();
    private static RepeatTask coolDownUpdateTask = RepeatTask.NONE_TASK;
    private static Identifier cooldownId;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cRoot = CommandManager.literal("randomtp").requires(ServerCommandSource::isExecutedByPlayer)
                .executes(RandomTpCommand::randomtp);

        var cNode = dispatcher.register(cRoot);

        var cRoot_redir = CommandManager.literal("rtp").requires(ServerCommandSource::isExecutedByPlayer)
                .executes(RandomTpCommand::randomtp).redirect(cNode);

        dispatcher.register(cRoot_redir);

        ServerLivingEntityEvents.ALLOW_DAMAGE.register(RandomTpCommand::onLivingEntityDamaged);
        ServerGameEvents.STAGE_TRIGGERED.register(RandomTpCommand::onStageTriggered);
    }

    private static void onStageTriggered(GameManager gameManager, @Nullable Identifier id) {
        if (id == null) {
            cooldownId = null;
        } else if (Variables.config.randomTp().cooldownMap().get(id) != null) {
            cooldownId = id;
        }
    }

    private static boolean onLivingEntityDamaged(LivingEntity entity, DamageSource source, float amount) {
        if (entity instanceof ServerPlayerEntity player) {
            UUID uuid = PlayerUtil.getAuthUUID(player);
            if (Variables.gameManager.getGameStage().isGaming() && source.getSource() != null
                    && source.getSource().isPlayer()
                    && Variables.gameManager.isParticipant(uuid)) {
                RandomTpCommand.updateCooldown(uuid, getConfigCooldown());
            }
        }
        return true;
    }

    public static int getConfigCooldown() {
        int cooldown = Variables.config.randomTp().defaultCooldown().toTicks();
        if (cooldownId != null
                && Variables.config.randomTp().cooldownMap().get(cooldownId) != null) {
            cooldown = Variables.config.randomTp().cooldownMap().get(cooldownId).toTicks();
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
