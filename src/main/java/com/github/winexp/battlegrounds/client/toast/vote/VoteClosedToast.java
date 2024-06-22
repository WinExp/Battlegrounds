package com.github.winexp.battlegrounds.client.toast.vote;

import com.github.winexp.battlegrounds.client.toast.ImmutableSimpleToast;
import com.github.winexp.battlegrounds.discussion.vote.Vote;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VoteClosedToast extends ImmutableSimpleToast {
    private static final Identifier TEXTURE = new Identifier("battlegrounds", "toast/vote");

    public VoteClosedToast(Vote vote, Vote.CloseReason closeReason) {
        super(
                ToastType.CLOSED, TEXTURE,
                Text.translatable("vote.toast.closed.title", vote.getName()),
                Text.translatable("vote.toast.closed.subtitle", Text.translatableWithFallback("vote.close_reason." + closeReason.name().toLowerCase(), closeReason.name()))
        );
    }
}
