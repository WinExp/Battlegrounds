package com.github.winexp.battlegrounds.command;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.command.argument.PVPModeArgumentType;
import com.github.winexp.battlegrounds.discussion.vote.*;
import com.github.winexp.battlegrounds.entity.projectile.thrown.FlashBangEntity;
import com.github.winexp.battlegrounds.game.GameProperties;
import com.github.winexp.battlegrounds.game.GameTrigger;
import com.github.winexp.battlegrounds.game.PVPMode;
import com.github.winexp.battlegrounds.registry.ModRegistryKeys;
import com.github.winexp.battlegrounds.resource.listener.DataPackResourceReloadListener;
import com.github.winexp.battlegrounds.util.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BattlegroundsCommand {
    public static void registerRoot(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        var cRoot = literal("battlegrounds");
        var cPvpMode = registerPvpMode();
        var cGameMode = registerGameMode();
        var cTrigger = registerTrigger(registryAccess);
        var cStart = registerStart();
        var cStop = literal("stop").requires(source ->
                source.hasPermissionLevel(2)).executes(BattlegroundsCommand::executeStop);
        var cReload = literal("reload").requires(source ->
                source.hasPermissionLevel(2)).executes(BattlegroundsCommand::executeReload);
        var cFlash = registerSummonFlash();
        var cNode = dispatcher.register(cRoot.then(cPvpMode).then(cGameMode).then(cTrigger).then(cStart).then(cStop).then(cReload).then(cFlash));
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

    public static ArgumentBuilder<ServerCommandSource, ?> registerTrigger(CommandRegistryAccess registryAccess) {
        var cTrigger = literal("trigger").requires(source ->
                source.hasPermissionLevel(2));
        var aTrigger = argument("trigger", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, ModRegistryKeys.GAME_TRIGGER))
                .executes(BattlegroundsCommand::executeTrigger);
        return cTrigger.then(aTrigger);
    }

    public static int executeTrigger(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        RegistryEntry<GameTrigger> entry = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "trigger", ModRegistryKeys.GAME_TRIGGER);
        entry.value().apply(Variables.gameManager);
        return 1;
    }

    public static ArgumentBuilder<ServerCommandSource, ?> registerStart() {
        var cStart = literal("start");
        var aProp = argument("game_properties", IdentifierArgumentType.identifier())
                .suggests(((context, builder) ->
                        CommandSource.suggestMatching(DataPackResourceReloadListener.GAME_PROPERTIES.keySet().stream().map(Identifier::toString), builder)))
                .executes(BattlegroundsCommand::executeStart);
        return cStart.then(aProp);
    }

    public static ArgumentBuilder<ServerCommandSource, ?> registerSummonFlash() {
        var cSummon = literal("summonflash").requires(source ->
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
                .executes(context -> executeChangeGameMode(context, true));
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
        Identifier id = IdentifierArgumentType.getIdentifier(context, "game_properties");
        if (!DataPackResourceReloadListener.GAME_PROPERTIES.containsKey(id)) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }
        GameProperties gameProperties = DataPackResourceReloadListener.GAME_PROPERTIES.get(id);
        if (VoteManager.INSTANCE.containsVote(VotePreset.START_GAME.identifier())) {
            source.sendFeedback(() -> Text.translatable("vote.battlegrounds.duplicate.feedback")
                    .formatted(Formatting.RED), false);
        } else {
            VoteInstance instance = VoteInstance.createWithPreset(VotePreset.START_GAME, source.getDisplayName());
            instance.putParameter("game_properties", gameProperties);
            if (!VoteManager.INSTANCE.openVote(instance, source.getServer().getPlayerManager().getPlayerList())) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();
            }
        }
        return 1;
    }

    private static int executeRespawnPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        if (VoteManager.INSTANCE.containsVote(VotePreset.RESPAWN_PLAYER.identifier())) {
            source.sendFeedback(() -> Text.translatable("vote.battlegrounds.duplicate.feedback")
                    .formatted(Formatting.RED), false);
        } else {
            VoteInstance voteInstance = VoteInstance.createWithPreset(VotePreset.RESPAWN_PLAYER, source.getDisplayName());
            voteInstance.callback(new VoteCallback() {
                @Override
                public void onPlayerVoted(VoteInstance voteInstance, ServerPlayerEntity player, boolean result) {
                }

                @Override
                public void onClosed(VoteInstance voteInstance, CloseReason closeReason) {
                    if (closeReason == CloseReason.ACCEPTED) {

                    }
                }
            });
        }
        return 1;
    }

    private static int executeStop(CommandContext<ServerCommandSource> context) {
        VoteManager.INSTANCE.closeAllVotes();
        TaskScheduler.INSTANCE.cancelAllTasks();
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
