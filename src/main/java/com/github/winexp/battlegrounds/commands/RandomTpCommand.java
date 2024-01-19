package com.github.winexp.battlegrounds.commands;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.helper.task.Task;
import com.github.winexp.battlegrounds.helper.task.TaskLater;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.TextUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class RandomTpCommand {
    private static HashMap<UUID, TaskLater> cooldownTasks = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        var cRoot = CommandManager.literal("randomtp").requires(ServerCommandSource::isExecutedByPlayer)
                .executes(RandomTpCommand::randomtp);

        var cNode = dispatcher.register(cRoot);

        var cRoot_redir = CommandManager.literal("rtp").requires(ServerCommandSource::isExecutedByPlayer)
                .executes(RandomTpCommand::randomtp).redirect(cNode);

        dispatcher.register(cRoot_redir);
    }

    private static int randomtp(CommandContext<ServerCommandSource> context){
        ServerCommandSource source = context.getSource();
        UUID uuid = source.getPlayer().getGameProfile().getId();
        if (!cooldownTasks.containsKey(uuid)) cooldownTasks.put(uuid, TaskLater.NONE_TASK);
        TaskLater cooldownTask = cooldownTasks.get(uuid);
        long delay = cooldownTask.getDelay();
        if (delay > 0){
            source.sendFeedback(() -> TextUtil.translatableWithColor("battlegrounds.command.randomtp.delay.feedback",
                    TextUtil.RED, delay / 20), false);
        }
        else{
            source.sendFeedback(() -> TextUtil.translatableWithColor("battlegrounds.command.randomtp.feedback",
                    TextUtil.GOLD), false);
            PlayerUtil.randomTeleport(Battlegrounds.server.getOverworld(), Objects.requireNonNull(source.getPlayer()));
            cooldownTask = new TaskLater(Task.NONE_RUNNABLE, Battlegrounds.config.randomTpCooldownTicks);
            cooldownTasks.put(uuid, cooldownTask);
            Battlegrounds.taskScheduler.runTask(cooldownTask);
        }

        return 1;
    }
}
