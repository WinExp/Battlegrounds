package com.github.winexp.battlegrounds.command;

import com.github.winexp.battlegrounds.task.TaskExecutor;
import com.github.winexp.battlegrounds.task.RepeatTask;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.Variables;
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

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class RandomTpCommand {
    private static final HashMap<UUID, Long> cooldownTimers = new HashMap<>();
    private static RepeatTask coolDownUpdateTask = RepeatTask.NONE_TASK;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cRoot = CommandManager.literal("randomtp").requires(ServerCommandSource::isExecutedByPlayer)
                .executes(RandomTpCommand::randomtp);

        var cNode = dispatcher.register(cRoot);

        var cRoot_redir = CommandManager.literal("rtp").requires(ServerCommandSource::isExecutedByPlayer)
                .executes(RandomTpCommand::randomtp).redirect(cNode);

        dispatcher.register(cRoot_redir);

        ServerLivingEntityEvents.ALLOW_DAMAGE.register(RandomTpCommand::allowLivingEntityDamage);
    }

    public static boolean allowLivingEntityDamage(LivingEntity entity, DamageSource source, float amount) {
        if (entity instanceof ServerPlayerEntity player) {
            if (Variables.progress.gameStage.isStarted() && source.getSource() != null
                    && source.getSource().isPlayer() && Variables.progress.isInGame(PlayerUtil.getUUID(player))) {
                RandomTpCommand.setCooldown(player, Variables.config.cooldown.randomTpDamagedCooldownTicks);
            }
        }
        return true;
    }

    public static void setCooldown(ServerPlayerEntity player, long cooldown) {
        UUID uuid = PlayerUtil.getUUID(player);
        Long currentCooldown;
        if ((currentCooldown = cooldownTimers.putIfAbsent(uuid, cooldown)) != null) {
            cooldownTimers.put(uuid, Math.max(currentCooldown, cooldown));
        }
    }

    private static int randomtp(CommandContext<ServerCommandSource> context) {
        if (coolDownUpdateTask == RepeatTask.NONE_TASK) {
            coolDownUpdateTask = new RepeatTask(() -> cooldownTimers.forEach((uuid, ticks) -> {
                if (ticks <= 0) return;
                cooldownTimers.put(uuid, ticks - 1);
            }), 0, 1);
        }
        TaskExecutor.INSTANCE.execute(coolDownUpdateTask);

        ServerCommandSource source = context.getSource();
        assert source.getPlayer() != null;
        UUID uuid = PlayerUtil.getUUID(source.getPlayer());
        if (!cooldownTimers.containsKey(uuid)) cooldownTimers.put(uuid, 0L);
        Long cooldown = cooldownTimers.get(uuid);
        if (cooldown > 0) {
            source.sendFeedback(() -> Text.translatable("battlegrounds.command.randomtp.delay.feedback", cooldown / 20)
                    .formatted(Formatting.RED), false);
        } else {
            source.sendFeedback(() -> Text.translatable("battlegrounds.command.randomtp.feedback")
                    .formatted(Formatting.GOLD), false);
            PlayerUtil.randomTeleport(Variables.server.getOverworld(), Objects.requireNonNull(source.getPlayer()));
            cooldownTimers.put(uuid, Variables.config.cooldown.randomTpCooldownTicks);
        }

        return 1;
    }
}
