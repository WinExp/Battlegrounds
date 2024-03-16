package com.github.winexp.battlegrounds.client.toast;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.discussion.vote.VoteSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VoteClosedToast implements Toast {
    private static final Identifier TEXTURE = new Identifier("battlegrounds", "toast/vote");
    private final VoteInfo voteInfo;
    private final VoteSettings.CloseReason closeReason;

    public VoteClosedToast(VoteInfo voteInfo, VoteSettings.CloseReason closeReason) {
        this.voteInfo = voteInfo;
        this.closeReason = closeReason;
    }

    @Override
    public int getWidth() {
        return 175;
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        context.drawGuiTexture(TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        context.drawText(manager.getClient().textRenderer, Text.translatable("gui.battlegrounds.vote.closed.toast.title",
                this.voteInfo.name, this.closeReason.name()), 10, 7, Colors.WHITE, false);
        context.drawText(manager.getClient().textRenderer, Text.translatable("gui.battlegrounds.vote.closed.toast.subtitle"),
                10, 18, Colors.GRAY, false);
        return (double) startTime >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }
}
