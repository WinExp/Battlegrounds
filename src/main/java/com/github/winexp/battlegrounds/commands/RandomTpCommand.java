package com.github.winexp.battlegrounds.commands;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.TextUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class RandomTpCommand {
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
        source.sendFeedback(() -> TextUtil.translatableWithColor("battlegrounds.command.randomtp.feedback",
                TextUtil.GOLD), false);
        PlayerUtil.randomTeleport(Battlegrounds.server.getOverworld(), source.getPlayer());

        return 1;
    }
}
