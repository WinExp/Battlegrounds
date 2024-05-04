package com.github.winexp.battlegrounds.client.toast.vote;

import com.github.winexp.battlegrounds.discussion.vote.CloseReason;
import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.util.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VoteClosedToast extends SimpleToast {
    private static final Identifier TEXTURE = new Identifier("battlegrounds", "toast/vote");

    public VoteClosedToast(VoteInfo voteInfo, CloseReason closeReason) {
        super(
                Text.translatable("gui.battlegrounds.vote.closed.toast.title", voteInfo.name,
                        Text.translatableWithFallback("vote.battlegrounds.closed.reason." + closeReason.name().toLowerCase(), closeReason.name())),
                Text.translatable("gui.battlegrounds.vote.closed.toast.subtitle"),
                Duration.withSeconds(5)
        );
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }
}
