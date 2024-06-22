package com.github.winexp.battlegrounds.client.toast.vote;

import com.github.winexp.battlegrounds.client.toast.ImmutableSimpleToast;
import com.github.winexp.battlegrounds.discussion.vote.Vote;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VoteOpenedToast extends ImmutableSimpleToast {
    private static final Identifier TEXTURE = new Identifier("battlegrounds", "toast/vote");

    public VoteOpenedToast(Vote vote) {
        super(
                ToastType.OPENED, TEXTURE,
                Text.translatable("vote.toast.opened.title", vote.getName()),
                vote.getDescription()
        );
    }
}
