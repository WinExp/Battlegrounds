package com.github.winexp.battlegrounds.client.toast;

import com.github.winexp.battlegrounds.util.time.Duration;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.Collection;

public abstract class SimpleToast extends AutoWidthToast {
    private final Text title;
    private final Text subtitle;
    private final Duration displayDuration;

    protected SimpleToast(Text title, Text subtitle, Duration displayDuration) {
        this.title = title;
        this.subtitle = subtitle;
        this.displayDuration = displayDuration;
    }

    public abstract Identifier getTexture();

    @Override
    public final Collection<Text> getLines() {
        return ImmutableList.of(this.title, this.subtitle);
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        TextRenderer textRenderer = manager.getClient().textRenderer;
        context.drawGuiTexture(this.getTexture(), 0, 0, this.getWidth(), this.getHeight());
        context.drawText(textRenderer, this.title, 10, 7, Colors.WHITE, false);
        context.drawText(textRenderer, this.subtitle, 10, 18, Colors.GRAY, false);
        return (double) startTime >= this.displayDuration.toMillis() * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }
}
