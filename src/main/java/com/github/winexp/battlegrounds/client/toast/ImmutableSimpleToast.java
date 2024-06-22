package com.github.winexp.battlegrounds.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ImmutableSimpleToast extends SimpleToast {
    private final Type type;
    private final Identifier texture;
    private final Text title;
    private final Text subtitle;

    protected ImmutableSimpleToast(Type type, Identifier texture, Text title, Text subtitle) {
        this.type = type;
        this.texture = texture;
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    public final Type getType() {
        return this.type;
    }

    @Override
    public final Text getTitle() {
        return this.title;
    }

    @Override
    public final Text getSubtitle() {
        return this.subtitle;
    }

    @Override
    public final Identifier getTexture() {
        return this.texture;
    }
}
