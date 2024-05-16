package com.github.winexp.battlegrounds.client.toast.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.util.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VoteOpenedToast extends SimpleToast {
    private static final Identifier TEXTURE = new Identifier("battlegrounds", "toast/vote");

    public VoteOpenedToast(VoteInstance voteInstance) {
        super(
                Text.translatable("gui.battlegrounds.vote.opened.toast.title", voteInstance.getName()),
                Text.translatable("gui.battlegrounds.vote.opened.toast.subtitle"),
                Duration.withSeconds(5)
        );
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }
}
