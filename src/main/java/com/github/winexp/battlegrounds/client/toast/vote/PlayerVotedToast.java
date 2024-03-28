package com.github.winexp.battlegrounds.client.toast.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.util.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PlayerVotedToast extends SimpleToast {
    private static final Identifier TEXTURE = new Identifier("battlegrounds", "toast/vote");

    public PlayerVotedToast(Text playerName, VoteInfo voteInfo, boolean result) {
        super(
                Text.translatable("gui.battlegrounds.vote.player_voted.toast.title", playerName, voteInfo.name,
                        result ? Text.translatable("gui.battlegrounds.vote.accept") : Text.translatable("gui.battlegrounds.vote.deny")),
                Text.translatable("gui.battlegrounds.vote.player_voted.toast.subtitle"),
                Duration.withSeconds(5)
        );
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }
}
