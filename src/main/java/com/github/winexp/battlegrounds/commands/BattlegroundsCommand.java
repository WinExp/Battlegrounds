package com.github.winexp.battlegrounds.commands;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.events.vote.VoteCompletedCallback;
import com.github.winexp.battlegrounds.helper.GameHelper;
import com.github.winexp.battlegrounds.helper.TeamHelper;
import com.github.winexp.battlegrounds.helper.VoteHelper;
import com.github.winexp.battlegrounds.helper.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@SuppressWarnings("SameReturnValue")
public class BattlegroundsCommand {
    private final static TeamHelper teamHelper = new TeamHelper(null);
    private final static VoteHelper voter = new VoteHelper();

    public static void registerRoot(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cRoot = literal("battlegrounds").executes(BattlegroundsCommand::executeHelp);
        var cHelp = literal("help").executes(BattlegroundsCommand::executeHelp);
        var cStart = literal("start").executes(BattlegroundsCommand::executeStart);
        var cStop = literal("stop").requires(source ->
                source.hasPermissionLevel(2)).executes(BattlegroundsCommand::executeStop);
        var cAccept = literal("accept").requires(
                ServerCommandSource::isExecutedByPlayer).executes(BattlegroundsCommand::executeAccept);
        var cDeny = literal("deny").requires(
                ServerCommandSource::isExecutedByPlayer).executes(BattlegroundsCommand::executeDeny);
        var cReload = literal("reload").requires(source ->
                source.hasPermissionLevel(2)).executes(BattlegroundsCommand::executeReload);
        var cMode = registerGameMode();
        var cNode = dispatcher.register(cRoot.then(cMode).then(cHelp).then(cStart).then(cStop).then(cAccept).then(cDeny).then(cReload));

        // 命令缩写
        var cRoot_redir = literal("bg").executes(BattlegroundsCommand::executeHelp).redirect(cNode);
        dispatcher.register(cRoot_redir);
    }

    public static ArgumentBuilder<ServerCommandSource, ?> registerGameMode() {
        var cRoot = literal("gamemode");
        var aMode = argument("gamemode", GameModeArgumentType.gameMode()).executes((context) ->
                executeChangeGameMode(context, true));
        var aPlayer = argument("players", EntityArgumentType.players()).executes((context ->
                executeChangeGameMode(context, false)));

        return cRoot.then(aMode.then(aPlayer));
    }

    private static int executeChangeGameMode(CommandContext<ServerCommandSource> context, boolean isSelf) throws CommandSyntaxException {
        GameMode gameMode = GameModeArgumentType.getGameMode(context, "gamemode");
        if (isSelf && context.getSource().isExecutedByPlayer()) {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            PlayerUtil.setGameMode(player, gameMode);
        }
        else if (!isSelf) {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
            for (ServerPlayerEntity player : players) {
                PlayerUtil.setGameMode(player, gameMode);
            }
        }
        return 1;
    }

    private static int executeStart(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (voter.isVoting()) {
            source.sendFeedback(() -> TextUtil.translatableWithColor(
                    "battlegrounds.vote.already_voting.feedback", TextUtil.RED), false);
            return 1;
        } else if (voter.getCooldown() > 0) {
            source.sendFeedback(() -> TextUtil.translatableWithColor(
                    "battlegrounds.vote.cooldown.feedback", TextUtil.RED, voter.getCooldown() / 20), false);
            return 1;
        }
        voter.startVote(Variables.server.getPlayerManager().getPlayerList().toArray(new ServerPlayerEntity[0]));
        Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.command.start.broadcast", TextUtil.GREEN, source.getName())
                .append(TextFactory.LINEFEED)
                .append(TextFactory.ACCEPT_BUTTON)
                .append("   ")
                .append(TextFactory.DENY_BUTTON), false);

        return 1;
    }

    private static int executeStop(CommandContext<ServerCommandSource> context) {
        voter.stopVote(VoteCompletedCallback.Reason.MANUAL);
        TaskScheduler.INSTANCE.stopAllTask();
        GameHelper.INSTANCE.stopGame();
        Constants.LOGGER.info("已停止队列中所有任务");

        return 1;
    }

    private static int executeAccept(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        assert player != null;
        if (!voter.isVoting()) {
            source.sendFeedback(() -> TextUtil.translatableWithColor(
                    "battlegrounds.vote.not_voting.feedback", TextUtil.RED), false);
            return 1;
        }
        voter.acceptVote(player);

        return 1;
    }

    private static int executeDeny(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        assert player != null;
        if (!voter.isVoting()) {
            source.sendFeedback(() -> TextUtil.translatableWithColor(
                    "battlegrounds.vote.not_voting.feedback", TextUtil.RED), false);
            return 1;
        }
        voter.denyVote(player);

        return 1;
    }

    private static int executeReload(CommandContext<ServerCommandSource> context) {
        Battlegrounds.reload();
        context.getSource().sendFeedback(() -> TextUtil.translatableWithColor(
                "battlegrounds.command.reload.success", TextUtil.GREEN), false);

        return 1;
    }

    private static int executeHelp(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> TextFactory.HELP_TEXT, false);

        return 1;
    }
}
