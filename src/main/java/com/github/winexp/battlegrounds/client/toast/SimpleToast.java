package com.github.winexp.battlegrounds.client.toast;

import com.github.winexp.battlegrounds.client.util.render.TextDrawer;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.Collection;

@Environment(EnvType.CLIENT)
public abstract class SimpleToast extends AutoWidthToast {
    protected SimpleToast() {
    }

    public abstract Type getType();

    public abstract Text getTitle();

    public abstract Text getSubtitle();

    public abstract Identifier getTexture();

    @Override
    public final Collection<Text> getLines() {
        return ImmutableList.of(this.getTitle(), this.getSubtitle());
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        TextRenderer textRenderer = manager.getClient().textRenderer;
        context.drawGuiTexture(this.getTexture(), 0, 0, this.getWidth(), this.getHeight());
        context.drawText(textRenderer, TextDrawer.trim(this.getTitle(), this.getWidth() - this.getOffset(), 1.0F), 10, 7, Colors.WHITE, false);
        context.drawText(textRenderer, TextDrawer.trim(this.getSubtitle(), this.getWidth() - this.getOffset(), 1.0F), 10, 18, Colors.LIGHT_GRAY, false);
        return (double) startTime >= this.getType().getDisplayDuration().getMillis() * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    public interface Type {
        Duration getDisplayDuration();
    }
}
