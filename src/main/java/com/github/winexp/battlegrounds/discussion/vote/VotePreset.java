package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.game.GameManager;
import com.github.winexp.battlegrounds.util.Variables;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public record VotePreset(Identifier identifier, Text name, Text description, VoteSettings voteSettings) {
    public static final VotePreset START_GAME = new VotePreset(
            new Identifier("battlegrounds", "start_game"),
            Text.literal("开始游戏").formatted(Formatting.GREEN),
            Text.literal("开始游戏的投票，需全玩家通过"),
            new VoteSettings(
                    (voteInstance, closeReason) -> {
                        if (closeReason == VoteSettings.CloseReason.ACCEPTED) {
                            GameManager.INSTANCE.prepareResetWorlds(voteInstance.getParticipants());
                        }
                    },
                    (voteInstance, player, result) -> {
                        if (result) {
                            Variables.server.getPlayerManager().broadcast(Text.translatable("battlegrounds.command.accept.broadcast",
                                            player.getDisplayName(), voteInstance.getAcceptedNum(), voteInstance.getTotal())
                                    .formatted(Formatting.GREEN), false);
                        } else {
                            Variables.server.getPlayerManager().broadcast(Text.translatable("battlegrounds.command.deny.broadcast",
                                            player.getDisplayName())
                                    .formatted(Formatting.GOLD), false);
                        }
                    },
                    VoteSettings.VoteMode.ALL_ACCEPT,
                    Variables.config.cooldown.startGameVoteCooldownTicks
    ));
}
