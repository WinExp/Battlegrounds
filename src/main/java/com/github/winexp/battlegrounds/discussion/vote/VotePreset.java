package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.util.Variables;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public record VotePreset(Identifier identifier, Text name, Text description, VoteSettings voteSettings) {
    public static final VotePreset START_GAME = new VotePreset(
            new Identifier("battlegrounds", "start_game"),
            Text.translatable("vote.battlegrounds.start_game.title").formatted(Formatting.GREEN),
            Text.translatable("vote.battlegrounds.start_game.description"),
            new VoteSettings(
                    VoteSettings.VoteMode.ALL_ACCEPT,
                    Variables.config.votes()
                            .get(new Identifier("battlegrounds", "start_game"))
                            .timeout(),
                    false
    ));
}
