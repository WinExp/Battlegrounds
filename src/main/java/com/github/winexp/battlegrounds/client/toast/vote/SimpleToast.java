package com.github.winexp.battlegrounds.client.toast.vote;

import com.github.winexp.battlegrounds.util.time.Duration;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public abstract class SimpleToast extends AutoWidthToast {
    private final Supplier<Text> title;
    private final Supplier<Text> subtitle;
    private final Duration displayDuration;

    protected SimpleToast(Text title, Text subtitle, Duration displayDuration) {
        this(() -> title, () -> subtitle, displayDuration);
    }

    protected SimpleToast(Supplier<Text> title, Supplier<Text> subtitle, Duration displayDuration) {
        this.title = title;
        this.subtitle = subtitle;
        this.displayDuration = displayDuration;
    }

    public abstract Identifier getTexture();

    @Override
    public final int getOffset() {
        return 15;
    }

    @Override
    public final Collection<Text> getLines() {
        return List.of(title.get(), subtitle.get());
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        TextRenderer textRenderer = manager.getClient().textRenderer;
        context.drawGuiTexture(this.getTexture(), 0, 0, this.getWidth(), this.getHeight());
        context.drawText(textRenderer, this.title.get(), 10, 7, Colors.WHITE, false);
        context.drawText(textRenderer, this.subtitle.get(), 10, 18, Colors.GRAY, false);
        return (double) startTime >= this.displayDuration.toMillis() * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }
}
