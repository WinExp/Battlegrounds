package com.github.winexp.battlegrounds.client.toast.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VoteOpenedToast implements Toast {
    private static final Identifier TEXTURE = new Identifier("toast/advancement");
    private final VoteInfo voteInfo;

    public VoteOpenedToast(VoteInfo voteInfo) {
        this.voteInfo = voteInfo;
    }

    @Override
    public int getWidth() {
        return 175;
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        context.drawGuiTexture(TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        context.drawText(manager.getClient().textRenderer, Text.translatable("gui.battlegrounds.vote.opened.toast.title",
                this.voteInfo.name), 10, 7, Colors.WHITE, false);
        context.drawText(manager.getClient().textRenderer, Text.translatable("gui.battlegrounds.vote.opened.toast.subtitle"),
                10, 18, Colors.GRAY, false);
        return (double) startTime >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }
}
