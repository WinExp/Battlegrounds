package com.github.winexp.battlegrounds.client.toast.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.util.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PlayerVotedToast extends SimpleToast {
    private static final Identifier TEXTURE = new Identifier("battlegrounds", "toast/vote");

    public PlayerVotedToast(Text playerName, VoteInstance voteInstance, boolean result) {
        super(
                Text.translatable("gui.battlegrounds.vote.player_voted.toast.title", playerName,
                        voteInstance.getName(), getResultText(result)),
                Text.translatable("gui.battlegrounds.vote.player_voted.toast.subtitle"),
                Duration.withSeconds(5)
        );
    }

    private static Text getResultText(boolean result) {
        return result ? Text.translatable("gui.battlegrounds.vote.accept")
                : Text.translatable("gui.battlegrounds.vote.deny");
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }
}
