package com.github.winexp.battlegrounds.commands;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.events.vote.VoteCompletedCallback;
import com.github.winexp.battlegrounds.helper.VoteHelper;
import com.github.winexp.battlegrounds.helper.WorldHelper;
import com.github.winexp.battlegrounds.util.TextFactory;
import com.github.winexp.battlegrounds.util.TextUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class BattlegroundsCommand {
    public static VoteHelper voter = new VoteHelper();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        var cRoot = CommandManager.literal("battlegrounds").executes(BattlegroundsCommand::help);

        var cHelp = CommandManager.literal("help").executes(BattlegroundsCommand::help);

        var cStart = CommandManager.literal("start").executes(context -> {
            ServerCommandSource source = context.getSource();
            if (voter.isVoting()){
                source.sendFeedback(() -> TextUtil.translatableWithColor(
                        "battlegrounds.vote.already_voting.feedback", TextUtil.RED), false);
                return 1;
            }
            else if (voter.getCooldown() > 0){
                source.sendFeedback(() -> TextUtil.translatableWithColor(
                        "battlegrounds.vote.cooldown.feedback", TextUtil.RED, voter.getCooldown() / 20), false);
                return 1;
            }
            voter.startVote(Battlegrounds.server.getPlayerManager().getPlayerList().toArray(new ServerPlayerEntity[0]));
            Battlegrounds.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.command.start.broadcast", TextUtil.GREEN, source.getName())
                    .append(TextFactory.LINEFEED)
                    .append(TextFactory.ACCEPT_BUTTON)
                    .append("   ")
                    .append(TextFactory.DENY_BUTTON), false);

            return 1;
        });

        var cStop = CommandManager.literal("stop").requires(source ->
                source.hasPermissionLevel(4)).executes(context -> {
                    voter.stopVote(VoteCompletedCallback.Reason.MANUAL);

                    return 1;
        });

        var cAccept = CommandManager.literal("accept").requires(
                ServerCommandSource::isExecutedByPlayer).executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    if (!voter.isVoting()){
                        source.sendFeedback(() -> TextUtil.translatableWithColor(
                                "battlegrounds.vote.not_voting.feedback", TextUtil.RED), false);
                        return 1;
                    }
                    voter.acceptVote(player);

                    return 1;
        });

        var cDeny = CommandManager.literal("deny").requires(
                ServerCommandSource::isExecutedByPlayer).executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    if (!voter.isVoting()){
                        source.sendFeedback(() -> TextUtil.translatableWithColor(
                                "battlegrounds.vote.not_voting.feedback", TextUtil.RED), false);
                        return 1;
                    }
                    voter.denyVote(player);
                    return 1;
        });

        var cReload = CommandManager.literal("reload").executes(context -> {
            Battlegrounds.saveAndLoadConfigs();
            context.getSource().sendFeedback(() -> TextUtil.translatableWithColor(
                    "battlegrounds.command.reload.success", TextUtil.GREEN), false);

            return 1;
        });

        var cBorder = CommandManager.literal("border").executes(context -> {
            var helper = new WorldHelper(Battlegrounds.server.getOverworld());
            helper.setBorderSize(1000, 60000);

            return 1;
        });

        var cNode = dispatcher.register(cRoot.then(cBorder).then(cHelp).then(cStart).then(cStop).then(cAccept).then(cDeny).then(cReload));

        // 命令缩写
        var cRoot_redir = CommandManager.literal("bg").executes(BattlegroundsCommand::help).redirect(cNode);
        dispatcher.register(cRoot_redir);
    }

    private static int help(CommandContext<ServerCommandSource> context){
        context.getSource().sendFeedback(() -> TextFactory.HELP_TEXT, false);

        return 1;
    }
}
