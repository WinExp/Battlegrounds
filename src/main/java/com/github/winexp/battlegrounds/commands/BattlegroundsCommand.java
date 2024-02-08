package com.github.winexp.battlegrounds.commands;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.events.vote.VoteCompletedCallback;
import com.github.winexp.battlegrounds.helper.GameHelper;
import com.github.winexp.battlegrounds.helper.TeamHelper;
import com.github.winexp.battlegrounds.helper.VoteHelper;
import com.github.winexp.battlegrounds.helper.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.Environment;
import com.github.winexp.battlegrounds.util.TextFactory;
import com.github.winexp.battlegrounds.util.TextUtil;
import com.github.winexp.battlegrounds.util.Variable;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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
                source.hasPermissionLevel(4)).executes(BattlegroundsCommand::executeStop);
        var cAccept = literal("accept").requires(
                ServerCommandSource::isExecutedByPlayer).executes(BattlegroundsCommand::executeAccept);
        var cDeny = literal("deny").requires(
                ServerCommandSource::isExecutedByPlayer).executes(BattlegroundsCommand::executeDeny);
        var cReload = literal("reload").executes(BattlegroundsCommand::executeReload);
        var cNode = dispatcher.register(cRoot.then(cHelp).then(cStart).then(cStop).then(cAccept).then(cDeny).then(cReload));

        // 命令缩写
        var cRoot_redir = literal("bg").executes(BattlegroundsCommand::executeHelp).redirect(cNode);
        dispatcher.register(cRoot_redir);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildTeamCommand() {
        var cRoot = literal("team").requires(ServerCommandSource::isExecutedByPlayer);
        var cCreate = literal("create")
                .then(argument("teamName", StringArgumentType.word()))
                .executes(BattlegroundsCommand::executeTeamCreate);
        var cJoin = literal("join")
                .then(argument("teamName", StringArgumentType.word()))
                .executes(BattlegroundsCommand::executeTeamJoin);

        return null;
    }

    private static int executeTeamCreate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String teamName = StringArgumentType.getString(context, "teamName");
        if (teamHelper.getTeam(teamName) != null) throw new CommandSyntaxException(
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
                new LiteralMessage("队伍已存在")
        );
        Team team = teamHelper.addTeam(teamName);
        team.setFriendlyFireAllowed(false);
        team.setPrefix(Text.literal('[' + teamName + ']'));
        teamHelper.addPlayerToTeam(teamName, context.getSource().getPlayerOrThrow());

        return 1;
    }

    private static int executeTeamJoin(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String teamName = StringArgumentType.getString(context, "teamName");
        if (teamHelper.getTeam(teamName) == null) {
            throw new CommandSyntaxException(
                    CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
                    new LiteralMessage("队伍不存在")
            );
        } else if (teamHelper.getPlayerTeam(context.getSource().getPlayerOrThrow()) != null) {

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
        voter.startVote(Variable.INSTANCE.server.getPlayerManager().getPlayerList().toArray(new ServerPlayerEntity[0]));
        Variable.INSTANCE.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
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
        Environment.LOGGER.info("已停止队列中所有任务");

        return 1;
    }

    private static int executeAccept(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
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
