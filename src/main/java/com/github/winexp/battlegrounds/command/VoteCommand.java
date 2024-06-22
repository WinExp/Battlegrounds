package com.github.winexp.battlegrounds.command;

import com.github.winexp.battlegrounds.command.argument.VoteArgumentType;
import com.github.winexp.battlegrounds.discussion.vote.Vote;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.discussion.vote.VoteMode;
import com.github.winexp.battlegrounds.discussion.vote.VotePreset;
import com.github.winexp.battlegrounds.registry.ModRegistryKeys;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.*;

public class VoteCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        var cRoot = literal("vote").requires(source -> source.hasPermissionLevel(2));
        var cOpen = literal("open")
                .then(argument("group", IdentifierArgumentType.identifier())
                        .then(argument("name", StringArgumentType.string())
                                .then(argument("description", StringArgumentType.string())
                                        .then(argument("mode", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, ModRegistryKeys.VOTE_MODE))
                                                .then(argument("participants", EntityArgumentType.players())
                                                        .then(argument("expiration_duration", TimeArgumentType.time(1))
                                                                .executes(this::executeOpen)))))));
        var cOpenPreset = literal("openpreset")
                .then(argument("preset", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, ModRegistryKeys.VOTE_PRESET))
                        .then(argument("participants", EntityArgumentType.players())
                                .then(argument("expiration_duration", TimeArgumentType.time(1))
                                        .executes(this::executeOpenPreset))));
        var cClose = literal("close")
                .then(argument("vote", VoteArgumentType.vote())
                        .executes(this::executeClose));
        var cCloseAll = literal("closeall").executes(this::executeCloseAll);
        dispatcher.register(cRoot.then(cOpen).then(cOpenPreset).then(cClose).then(cCloseAll));
    }

    private int executeOpen(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier group = IdentifierArgumentType.getIdentifier(context, "group");
        Text name = Text.of(StringArgumentType.getString(context, "name"));
        Text description = Text.of(StringArgumentType.getString(context, "description"));
        VoteMode voteMode = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "mode", ModRegistryKeys.VOTE_MODE).value();
        Collection<UUID> participants = EntityArgumentType.getPlayers(context, "participants").stream().map(PlayerUtil::getAuthUUID).toList();
        int expirationDuration = context.getArgument("expiration_duration", Integer.class);
        new Vote(group, name, description, voteMode).open(participants, Duration.ofTicks(expirationDuration));
        return 1;
    }

    private int executeOpenPreset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        VotePreset votePreset = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "preset", ModRegistryKeys.VOTE_PRESET).value();
        Collection<UUID> participants = EntityArgumentType.getPlayers(context, "participants").stream().map(PlayerUtil::getAuthUUID).toList();
        int expirationDuration = context.getArgument("expiration_duration", Integer.class);
        new Vote(votePreset).open(participants, Duration.ofTicks(expirationDuration));
        return 1;
    }

    private int executeClose(CommandContext<ServerCommandSource> context) {
        Vote vote = VoteArgumentType.getVote(context, "vote");
        vote.close();
        return 1;
    }

    private int executeCloseAll(CommandContext<ServerCommandSource> context) {
        try {
            return VoteManager.INSTANCE.clearVotes();
        } catch (Exception e) {
            Constants.LOGGER.error(e);
            throw e;
        }
    }
}
