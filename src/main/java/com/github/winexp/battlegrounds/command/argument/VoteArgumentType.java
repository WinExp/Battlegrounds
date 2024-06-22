package com.github.winexp.battlegrounds.command.argument;

import com.github.winexp.battlegrounds.client.vote.ClientVoteManager;
import com.github.winexp.battlegrounds.discussion.vote.Vote;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VoteArgumentType extends UuidArgumentType {
    public VoteArgumentType() {
    }

    private static VoteManager getVoteManager() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? ClientVoteManager.INSTANCE : VoteManager.INSTANCE;
    }

    public static Vote getVote(CommandContext<ServerCommandSource> context, String name) {
        UUID uuid = UuidArgumentType.getUuid(context, name);
        return VoteManager.INSTANCE.getVote(uuid);
    }

    public static VoteArgumentType vote() {
        return new VoteArgumentType();
    }

    @Override
    public UUID parse(StringReader stringReader) throws CommandSyntaxException {
        UUID uuid = super.parse(stringReader);
        if (getVoteManager().getVote(uuid) == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(stringReader);
        }
        return uuid;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(getVoteManager().getVotes().stream().map(vote -> vote.getUuid().toString()), builder);
    }
}
