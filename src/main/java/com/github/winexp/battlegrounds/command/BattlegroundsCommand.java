package com.github.winexp.battlegrounds.command;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.command.argument.PVPModeArgumentType;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.discussion.vote.VotePreset;
import com.github.winexp.battlegrounds.entity.projectile.thrown.FlashBangEntity;
import com.github.winexp.battlegrounds.game.GameProperties;
import com.github.winexp.battlegrounds.game.PVPMode;
import com.github.winexp.battlegrounds.task.ServerTaskScheduler;
import com.github.winexp.battlegrounds.util.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BattlegroundsCommand {
    public static void registerRoot(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cRoot = literal("battlegrounds");
        var cPvpMode = registerPvpMode();
        var cGameMode = registerGameMode();
        var cStart = registerStart();
        var cStop = literal("stop").requires(source ->
                source.hasPermissionLevel(2)).executes(BattlegroundsCommand::executeStop);
        var cReload = literal("reload").requires(source ->
                source.hasPermissionLevel(2)).executes(BattlegroundsCommand::executeReload);
        var cFlash = registerSummonFlash();
        var cNode = dispatcher.register(cRoot.then(cPvpMode).then(cGameMode).then(cStart).then(cStop).then(cReload).then(cFlash));
        // 命令缩写
        var cRoot_redir = literal("bg").redirect(cNode);
        dispatcher.register(cRoot_redir);
    }

    public static ArgumentBuilder<ServerCommandSource, ?> registerPvpMode() {
        var cPvpMode = literal("pvp_mode").requires(ServerCommandSource::isExecutedByPlayer);
        var aPvpMode = argument("pvp_mode", PVPModeArgumentType.pvpMode())
                .executes(BattlegroundsCommand::executePvpMode);
        return cPvpMode.then(aPvpMode);
    }

    public static int executePvpMode(CommandContext<ServerCommandSource> context) {
        PVPMode pvpMode = PVPModeArgumentType.getPVPMode(context, "pvp_mode");
        Variables.gameManager.setPVPMode(pvpMode);
        return 1;
    }

    public static ArgumentBuilder<ServerCommandSource, ?> registerStart() {
        var cStart = literal("start");
        var aProp = argument("gameProperties", IdentifierArgumentType.identifier())
                .suggests(((context, builder) ->
                        CommandSource.suggestMatching(Constants.GAME_PROPERTIES.keySet().stream().map(Identifier::toString), builder)))
                .executes(BattlegroundsCommand::executeStart);
        return cStart.then(aProp);
    }

    public static ArgumentBuilder<ServerCommandSource, ?> registerSummonFlash() {
        var cSummon = literal("summonFlash").requires(source ->
                source.hasPermissionLevel(2)).executes(context -> executeSummonFlash(context, true));
        var aPos = argument("pos", Vec3ArgumentType.vec3()).executes(context -> executeSummonFlash(context, false));
        return cSummon.then(aPos);
    }

    private static int executeSummonFlash(CommandContext<ServerCommandSource> context, boolean isSelf) throws CommandSyntaxException {
        if (isSelf && context.getSource().isExecutedByPlayer()) {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            World world = player.getWorld();
            Vec3d pos = player.getPos();
            FlashBangEntity.summonFlash(world, pos);
        } else if (isSelf && !context.getSource().isExecutedByPlayer()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        } else {
            Vec3d pos = Vec3ArgumentType.getVec3(context, "pos");
            World world = Variables.server.getOverworld();
            FlashBangEntity.summonFlash(world, pos);
        }
        return 1;
    }

    public static ArgumentBuilder<ServerCommandSource, ?> registerGameMode() {
        var cRoot = literal("gamemode").requires(source ->
                source.hasPermissionLevel(2));
        var aMode = argument("gamemode", GameModeArgumentType.gameMode())
                .requires(ServerCommandSource::isExecutedByPlayer)
                .executes((context) ->
                executeChangeGameMode(context, true));
        var aPlayer = argument("players", EntityArgumentType.players()).executes((context ->
                executeChangeGameMode(context, false)));

        return cRoot.then(aMode.then(aPlayer));
    }

    private static int executeChangeGameMode(CommandContext<ServerCommandSource> context, boolean isSelf) throws CommandSyntaxException {
        GameMode gameMode = GameModeArgumentType.getGameMode(context, "gamemode");
        if (isSelf) {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            PlayerUtil.changeGameMode(player, gameMode);
        } else {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
            for (ServerPlayerEntity player : players) {
                PlayerUtil.changeGameMode(player, gameMode);
            }
        }
        return 1;
    }

    private static int executeStart(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Identifier id = IdentifierArgumentType.getIdentifier(context, "gameProperties");
        if (!Constants.GAME_PROPERTIES.containsKey(id)) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }
        GameProperties gameProperties = Constants.GAME_PROPERTIES.get(id);
        if (VoteManager.INSTANCE.containsVote(VotePreset.START_GAME.identifier())) {
            source.sendFeedback(() -> Text.translatable("vote.battlegrounds.already_voting.feedback")
                    .formatted(Formatting.RED), false);
        } else {
            VoteManager.INSTANCE.openVoteWithPreset(VotePreset.START_GAME, Variables.server.getPlayerManager().getPlayerList(),
                            Map.of("gameProperties", gameProperties))
                    .orElseThrow();
        }
        return 1;
    }

    private static int executeStop(CommandContext<ServerCommandSource> context) {
        VoteManager.INSTANCE.closeAllVotes();
        ServerTaskScheduler.INSTANCE.cancelAllTasks();
        Variables.gameManager.stopGame();
        context.getSource().sendFeedback(() -> Text.translatable("commands.battlegrounds.stop.feedback")
                .formatted(Formatting.GREEN), true);

        return 1;
    }

    private static int executeReload(CommandContext<ServerCommandSource> context) {
        Battlegrounds.INSTANCE.reload();
        context.getSource().sendFeedback(() -> Text.translatable("commands.battlegrounds.reload.feedback")
                .formatted(Formatting.GREEN), true);

        return 1;
    }
}
