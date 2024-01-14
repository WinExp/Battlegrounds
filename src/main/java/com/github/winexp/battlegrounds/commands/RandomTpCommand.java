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

public class RandomTpCommand {
    private static TaskLater cooldownTask = TaskLater.NONE_TASK;

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
        if (cooldownTask.getDelay() > 0){
            source.sendFeedback(() -> TextUtil.translatableWithColor("battlegrounds.command.randomtp.delay.feedback",
                    TextUtil.RED, cooldownTask.getDelay() / 20), false);
            return 1;
        }
        source.sendFeedback(() -> TextUtil.translatableWithColor("battlegrounds.command.randomtp.feedback",
                TextUtil.GOLD), false);
        PlayerUtil.randomTeleport(Battlegrounds.server.getOverworld(), source.getPlayer());
        cooldownTask = new TaskLater(Task.NONE_RUNNABLE, Battlegrounds.config.randomTpCooldownTicks);
        Battlegrounds.taskScheduler.runTask(cooldownTask);

        return 1;
    }
}
