package com.github.winexp.battlegrounds.client.toast.vote;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.text.Text;

import java.util.Collection;

public abstract class AutoWidthToast implements Toast {
    public abstract Collection<Text> getLines();

    public int getOffset() {
        return 15;
    }

    @Override
    public final int getWidth() {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int width = Toast.super.getWidth();
        for (Text text : this.getLines()) {
            width = Math.max(textRenderer.getWidth(text), width);
        }
        return width + this.getOffset();
    }
}
