package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.game.GameManager;
import com.github.winexp.battlegrounds.util.TextUtil;
import com.github.winexp.battlegrounds.util.Variables;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VotePresets {
    public static final Preset START_GAME = new Preset(
            new Identifier("battlegrounds", "start_game"),
            Text.of("开始游戏"),
            Text.of("开始游戏的投票，需全玩家通过"),
            new VoteSettings(
                    (voteInstance, closeReason) -> {
                        if (closeReason == VoteSettings.CloseReason.TIMEOUT) {
                            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                                    "battlegrounds.vote.timeout.broadcast", TextUtil.GOLD), false);
                        } else if (closeReason == VoteSettings.CloseReason.MANUAL) {
                            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                                    "battlegrounds.vote.manual.broadcast", TextUtil.GOLD), false);
                        } else if (closeReason == VoteSettings.CloseReason.ACCEPTED) {
                            GameManager.INSTANCE.prepareResetWorlds(voteInstance.getParticipants());
                        }
                    },
                    (voteInstance, player, result) -> {
                        if (Variables.server == null) return;
                        if (result) {
                            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                                    "battlegrounds.command.accept.broadcast", TextUtil.GREEN,
                                    player.getName(), voteInstance.getAcceptedNum(), voteInstance.getTotal()), false);
                        } else {
                            Variables.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                                    "battlegrounds.command.deny.broadcast", TextUtil.GOLD,
                                    player.getName(), voteInstance.getAcceptedNum(), voteInstance.getTotal()), false);
                        }
                    },
                    VoteSettings.VoteMode.ALL_ACCEPT,
                    Variables.config.cooldown.startGameVoteCooldownTicks
    ));

    public record Preset(Identifier identifier, Text name, Text description, VoteSettings voteSettings) {
    }
}
