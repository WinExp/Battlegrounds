package com.github.winexp.battlegrounds.client.util.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.joml.Matrix4f;

import java.util.List;

@Environment(EnvType.CLIENT)
public class TextDrawer {
    public static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().textRenderer;
    private final float lineSpacing;
    private float lineY;

    public TextDrawer() {
        this(1.0F);
    }

    public TextDrawer(float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public float getLineY() {
        return this.lineY;
    }

    public void setLineY(float lineY) {
        this.lineY = lineY;
    }

    private static void drawText(DrawContext context, OrderedText text, float x, float y, int color, float scale, boolean shadow) {
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        matrix4f.scale(scale);
        TEXT_RENDERER.draw(text, x, y, color, shadow, matrix4f, context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        context.draw();
        matrix4f.scale(1 / scale);
    }

    public static float getScaled(float num, float scale) {
        return num / scale;
    }

    public static OrderedText trim(Text text, int maxWidth, float scale) {
        maxWidth = Math.round(getScaled(maxWidth, scale));
        if (TEXT_RENDERER.getWidth(text) <= maxWidth) return text.asOrderedText();
        StringVisitable stringVisitable = TEXT_RENDERER.trimToWidth(text, maxWidth - TEXT_RENDERER.getWidth(ScreenTexts.ELLIPSIS));
        return Language.getInstance().reorder(StringVisitable.concat(stringVisitable, ScreenTexts.ELLIPSIS));
    }

    public void drawWrap(DrawContext context, StringVisitable text, int x, int maxWidth, int color, float scale, boolean shadow) {
        List<OrderedText> orderedTexts = TEXT_RENDERER.wrapLines(text, (int) getScaled(maxWidth, scale));
        for (OrderedText orderedText : orderedTexts) {
            this.draw(context, orderedText, x, color, scale, shadow);
        }
    }

    public void draw(DrawContext context, Text text, int x, int color, float scale, boolean shadow) {
        this.draw(context, text.asOrderedText(), x, color, scale, shadow);
    }

    public void draw(DrawContext context, OrderedText text, int x, int color, float scale, boolean shadow) {
        drawText(context, text, getScaled(x, scale), getScaled(this.lineY, scale), color, scale, shadow);
        this.wrap(scale);
    }

    public void wrap(float scale) {
        this.wrap(1, scale);
    }

    public void wrap(int lines, float scale) {
        this.lineY += (TEXT_RENDERER.fontHeight + this.lineSpacing) * lines * scale;
    }
}
