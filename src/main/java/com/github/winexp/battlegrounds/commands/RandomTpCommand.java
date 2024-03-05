package com.github.winexp.battlegrounds.commands;

import com.github.winexp.battlegrounds.helper.task.TaskScheduler;
import com.github.winexp.battlegrounds.helper.task.TaskTimer;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.TextUtil;
import com.github.winexp.battlegrounds.util.Variables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("SameReturnValue")
public class RandomTpCommand {
    private final static HashMap<UUID, Long> cooldownTimers = new HashMap<>();
    private static TaskTimer coolDownUpdateTask = TaskTimer.NONE_TASK;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cRoot = CommandManager.literal("randomtp").requires(ServerCommandSource::isExecutedByPlayer)
                .executes(RandomTpCommand::randomtp);

        var cNode = dispatcher.register(cRoot);

        var cRoot_redir = CommandManager.literal("rtp").requires(ServerCommandSource::isExecutedByPlayer)
                .executes(RandomTpCommand::randomtp).redirect(cNode);

        dispatcher.register(cRoot_redir);
    }

    public static void setCooldown(ServerPlayerEntity player, long cooldown) {
        UUID uuid = PlayerUtil.getUUID(player);
        Long currentCooldown;
        if ((currentCooldown = cooldownTimers.putIfAbsent(uuid, cooldown)) != null) {
            cooldownTimers.put(uuid, Math.max(currentCooldown, cooldown));
        }
    }

    private static int randomtp(CommandContext<ServerCommandSource> context) {
        if (coolDownUpdateTask == TaskTimer.NONE_TASK) {
            coolDownUpdateTask = new TaskTimer(() -> cooldownTimers.forEach((uuid, ticks) -> {
                if (ticks <= 0) return;
                cooldownTimers.put(uuid, ticks - 1);
            }), 0, 1);
        }
        TaskScheduler.INSTANCE.runTask(coolDownUpdateTask);

        ServerCommandSource source = context.getSource();
        assert source.getPlayer() != null;
        UUID uuid = PlayerUtil.getUUID(source.getPlayer());
        if (!cooldownTimers.containsKey(uuid)) cooldownTimers.put(uuid, 0L);
        Long cooldown = cooldownTimers.get(uuid);
        if (cooldown > 0) {
            source.sendFeedback(() -> TextUtil.translatableWithColor("battlegrounds.command.randomtp.delay.feedback",
                    TextUtil.RED, cooldown / 20), false);
        } else {
            source.sendFeedback(() -> TextUtil.translatableWithColor("battlegrounds.command.randomtp.feedback",
                    TextUtil.GOLD), false);
            PlayerUtil.randomTeleport(Variables.server.getOverworld(), Objects.requireNonNull(source.getPlayer()));
            cooldownTimers.put(uuid, Variables.config.cooldown.randomTpCooldownTicks);
        }

        return 1;
    }
}
