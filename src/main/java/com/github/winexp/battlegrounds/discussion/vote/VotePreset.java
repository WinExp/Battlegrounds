package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.game.GameProperties;
import com.github.winexp.battlegrounds.util.Variables;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public record VotePreset(Identifier identifier, Text name, Text description, VoteSettings voteSettings) {
    public static final VotePreset START_GAME = new VotePreset(
            new Identifier("battlegrounds", "start_game"),
            // TODO: 将 Text.literal() 替换为 Text.translatable()
            Text.literal("开始游戏").formatted(Formatting.GREEN),
            Text.literal("开始游戏的投票，需全玩家通过"),
            new VoteSettings(
                    (voteInstance, closeReason) -> {
                        if (closeReason == VoteSettings.CloseReason.ACCEPTED) {
                            GameProperties gameProperties = (GameProperties) voteInstance.getParameter("gameProperties").orElseThrow();
                            Variables.gameManager.setGameProperties(gameProperties);
                            Variables.gameManager.prepareToDeleteWorld(voteInstance.getParticipants());
                        }
                    },
                    (voteInstance, player, result) -> {},
                    VoteSettings.VoteMode.ALL_ACCEPT,
                    Variables.config.votes()
                            .get(new Identifier("battlegrounds", "start_game"))
                            .timeout()
    ));
}
