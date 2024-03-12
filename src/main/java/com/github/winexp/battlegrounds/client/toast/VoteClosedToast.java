package com.github.winexp.battlegrounds.client.toast;

import com.github.winexp.battlegrounds.network.packet.s2c.VoteClosedPacket;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class VoteClosedToast implements Toast {
    private static final Identifier TEXTURE = new Identifier("toast/advancement");
    private final VoteClosedPacket packet;

    public VoteClosedToast(VoteClosedPacket packet) {
        this.packet = packet;
    }

    @Override
    public int getWidth() {
        return 175;
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        context.drawGuiTexture(TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        context.drawText(manager.getClient().textRenderer, Text.translatable("gui.battlegrounds.vote.closed.toast.title",
                this.packet.voteInfo().name, this.packet.closeReason().name()), 10, 7, Colors.WHITE, false);
        context.drawText(manager.getClient().textRenderer, Text.translatable("gui.battlegrounds.vote.closed.toast.subtitle"),
                10, 18, Colors.GRAY, false);
        return (double) startTime >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }
}
