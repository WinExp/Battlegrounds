package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.registry.ModRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VotePresets {
    public static final VotePreset EMPTY = register("empty", new VotePreset(
            new Identifier("battlegrounds", "empty"),
            Text.empty(), Text.empty(), VoteModes.ALL_ACCEPT
    ));
    public static final VotePreset START_GAME = register("start_game", new VotePreset(
            new Identifier("battlegrounds", "game"),
            Text.of("Name1145141919810"),
            Text.of("Description"),
            VoteModes.ALL_ACCEPT
    ));

    private static VotePreset register(String id, VotePreset votePreset) {
        return Registry.register(ModRegistries.VOTE_PRESET, new Identifier("battlegrounds", id), votePreset);
    }

    public static void bootstrap() {
    }
}
