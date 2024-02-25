package com.github.winexp.battlegrounds.client.render;

import com.github.winexp.battlegrounds.entity.projectile.ChannelingArrowEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ChannelingArrowEntityRenderer extends ProjectileEntityRenderer<ChannelingArrowEntity> {
    public static final Identifier TEXTURE = new Identifier("battlegrounds", "textures/entity/projectiles/channeling_arrow.png");

    public ChannelingArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(ChannelingArrowEntity arrowEntity) {
        return TEXTURE;
    }
}