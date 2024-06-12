package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.game.GameProperties;
import com.github.winexp.battlegrounds.util.Variables;
import com.github.winexp.battlegrounds.util.time.Duration;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record VotePreset(Identifier identifier, Optional<Text> name, Optional<Text> description, VoteSettings voteSettings, Optional<VoteCallback> callback) {
    public static final VotePreset START_GAME = new VotePreset(
            new Identifier("battlegrounds", "start_game"),
            Optional.empty(), Optional.empty(),
            new VoteSettings(
                    VoteSettings.VoteMode.ALL_ACCEPT,
                    Duration.withSeconds(30),
                    false
            ),
            Optional.of(new VoteCallback() {
                @Override
                public void onPlayerVoted(VoteInstance voteInstance, ServerPlayerEntity player, boolean result) {
                }

                @Override
                public void onClosed(VoteInstance voteInstance, CloseReason closeReason) {
                    if (closeReason == CloseReason.ACCEPTED) {
                        GameProperties gameProperties = (GameProperties) voteInstance.getParameter("game_properties");
                        Variables.gameManager.setGameProperties(gameProperties);
                        Variables.gameManager.prepareToDeleteWorld(voteInstance.getParticipants());
                    }
                }
            })
    );
    public static final VotePreset RESPAWN_PLAYER = new VotePreset(
            new Identifier("battlegrounds", "respawn_player"),
            Optional.empty(), Optional.empty(),
            new VoteSettings(
                    VoteSettings.VoteMode.OVER_HALF_ACCEPT,
                    Duration.withSeconds(30),
                    true
            ),
            Optional.of(new VoteCallback() {
                @Override
                public void onPlayerVoted(VoteInstance voteInstance, ServerPlayerEntity player, boolean result) {
                }

                @Override
                public void onClosed(VoteInstance voteInstance, CloseReason closeReason) {
                    ServerPlayerEntity player = (ServerPlayerEntity) voteInstance.getParameter("player");
                    if (!Variables.gameManager.getGameStage().isGaming() || !Variables.gameManager.isParticipant(player)) return;
                    Variables.gameManager.spawnPlayer(player, 0);
                }
            })
    );

    public String getTranslationKey(String suffix) {
        return "vote." + this.identifier.getNamespace() + ".presets." + this.identifier.getPath() + "." + suffix;
    }
}
