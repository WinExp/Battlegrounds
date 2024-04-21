package com.github.winexp.battlegrounds.mixin.client;

import com.github.winexp.battlegrounds.client.util.ClientVariables;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class nameLabel_EntityRendererMixin {
    @Redirect(method = "hasLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;shouldRenderName()Z"))
    private boolean shouldRenderName(Entity instance) {
        return instance.shouldRenderName()
                && !(instance instanceof PlayerEntity && ClientVariables.gameConfig.displayPlayerNameLabel());
    }
}
