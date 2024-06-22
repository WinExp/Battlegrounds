package com.github.winexp.battlegrounds.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.text.Text;

import java.util.Collection;

@Environment(EnvType.CLIENT)
public abstract class AutoWidthToast implements Toast {
    public abstract Collection<Text> getLines();

    public int getOffset() {
        return 17;
    }

    public int getMaxWidth() {
        return 400;
    }

    @Override
    public int getWidth() {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int width = Toast.super.getWidth();
        for (Text text : this.getLines()) {
            width = Math.max(textRenderer.getWidth(text), width);
        }
        return Math.min(width, this.getMaxWidth()) + this.getOffset();
    }
}
