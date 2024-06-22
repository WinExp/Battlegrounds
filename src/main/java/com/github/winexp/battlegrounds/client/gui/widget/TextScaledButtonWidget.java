package com.github.winexp.battlegrounds.client.gui.widget;

import com.github.winexp.battlegrounds.client.util.render.TextDrawer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TextScaledButtonWidget extends ButtonWidget {
    private final TextDrawer textDrawer = new TextDrawer();
    private final float textScale;

    protected TextScaledButtonWidget(int x, int y, int width, int height, float textScale, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
        this.textScale = textScale;
    }

    public static Builder builder(Text message, PressAction onPress) {
        return new Builder(message, onPress);
    }

    @Override
    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
        this.textDrawer.setLineY(this.getY());
        this.textDrawer.draw(context, this.getMessage(), this.getX() + (this.width - 1 - textRenderer.getWidth(this.getMessage())) / 2, color, this.textScale, true);
    }

    public static class Builder extends ButtonWidget.Builder {
        private final Text message;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private float textScale = 1.0F;
        private NarrationSupplier narrationSupplier;

        public Builder(Text message, PressAction onPress) {
            super(message, onPress);
            this.narrationSupplier = ButtonWidget.DEFAULT_NARRATION_SUPPLIER;
            this.message = message;
            this.onPress = onPress;
        }

        @Override
        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        @Override
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        @Override
        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder textScale(float textScale) {
            this.textScale = textScale;
            return this;
        }

        @Override
        public Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        @Override
        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        @Override
        public Builder narrationSupplier(NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        @Override
        public TextScaledButtonWidget build() {
            TextScaledButtonWidget buttonWidget = new TextScaledButtonWidget(this.x, this.y, this.width, this.height, this.textScale, this.message, this.onPress, this.narrationSupplier);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }
}
