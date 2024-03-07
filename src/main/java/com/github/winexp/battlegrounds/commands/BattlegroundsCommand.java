package com.github.winexp.battlegrounds.commands;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.discussion.vote.VotePresets;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.helper.GameHelper;
import com.github.winexp.battlegrounds.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@SuppressWarnings("SameReturnValue")
public class BattlegroundsCommand {
    public static void registerRoot(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cRoot = literal("battlegrounds");
        var cStart = literal("start").executes(BattlegroundsCommand::executeStart);
        var cStop = literal("stop").requires(source ->
                source.hasPermissionLevel(2)).executes(BattlegroundsCommand::executeStop);
        var cReload = literal("reload").requires(source ->
                source.hasPermissionLevel(2)).executes(BattlegroundsCommand::executeReload);
        var cFlash = registerSummonFlash();
        var cMode = registerGameMode();
        var cNode = dispatcher.register(cRoot.then(cMode).then(cStart).then(cStop).then(cReload).then(cFlash));
        // 命令缩写
        var cRoot_redir = literal("bg").redirect(cNode);
        dispatcher.register(cRoot_redir);
    }

    public static ArgumentBuilder<ServerCommandSource, ?> registerSummonFlash() {
        var cSummon = literal("summonFlash").requires(source ->
                source.hasPermissionLevel(2)).executes(context -> executeSummonFlash(context, true));
        var aEntityType = argument("pos", Vec3ArgumentType.vec3()).executes(context -> executeSummonFlash(context, false));
        return cSummon.then(aEntityType);
    }

    private static int executeSummonFlash(CommandContext<ServerCommandSource> context, boolean isSelf) throws CommandSyntaxException {
        if (isSelf && context.getSource().isExecutedByPlayer()) {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            World world = player.getWorld();
            Vec3d pos = player.getPos();
            FlashBangEntity.sendFlash(world, pos);
        } else if (isSelf && !context.getSource().isExecutedByPlayer()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        } else {
            Vec3d pos = Vec3ArgumentType.getVec3(context, "pos");
            World world = Variables.server.getOverworld();
            FlashBangEntity.sendFlash(world, pos);
        }
        return 1;
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
        } else if (isSelf && !context.getSource().isExecutedByPlayer()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        } else {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
            for (ServerPlayerEntity player : players) {
                PlayerUtil.setGameMode(player, gameMode);
            }
        }
        return 1;
    }

    private static int executeStart(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (VoteManager.INSTANCE.containsVote(VotePresets.START_GAME.identifier())) {
            source.sendFeedback(() -> TextUtil.translatableWithColor(
                    "battlegrounds.vote.already_voting.feedback", TextUtil.RED), false);
            return 1;
        }
        VoteManager.INSTANCE.openVoteWithPreset(VotePresets.START_GAME, Variables.server.getPlayerManager().getPlayerList());
        Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.command.start.broadcast", TextUtil.GREEN, source.getName())
                .append(TextFactory.LINEFEED)
                .append(TextFactory.ACCEPT_BUTTON)
                .append("   ")
                .append(TextFactory.DENY_BUTTON), false);

        return 1;
    }

    private static int executeStop(CommandContext<ServerCommandSource> context) {
        VoteManager.INSTANCE.closeVote(VotePresets.START_GAME.identifier());
        TaskScheduler.INSTANCE.stopAllTask();
        GameHelper.INSTANCE.stopGame();
        Constants.LOGGER.info("已停止队列中所有任务");

        return 1;
    }

    private static int executeReload(CommandContext<ServerCommandSource> context) {
        Battlegrounds.INSTANCE.reload();
        context.getSource().sendFeedback(() -> TextUtil.translatableWithColor(
                "battlegrounds.command.reload.success", TextUtil.GREEN), false);

        return 1;
    }
}
