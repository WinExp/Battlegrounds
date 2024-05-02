package com.github.winexp.battlegrounds.mixin.client;

import com.github.winexp.battlegrounds.client.render.FlashRenderer;
import com.github.winexp.battlegrounds.client.util.ClientConstants;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class flashSprinting_ClientPlayerEntityMixin extends PlayerEntity {
    public flashSprinting_ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "canSprint", at = @At("RETURN"), cancellable = true)
    private void canSprint(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() && (ClientConstants.FLASH_RENDERER.getFlashStrength() < FlashRenderer.DISABLE_SPRINTING_STRENGTH
        || this.isSpectator()));
    }
}
