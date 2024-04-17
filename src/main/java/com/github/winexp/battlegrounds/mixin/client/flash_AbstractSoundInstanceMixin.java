package com.github.winexp.battlegrounds.mixin.client;

import com.github.winexp.battlegrounds.client.render.FlashRenderer;
import com.github.winexp.battlegrounds.client.util.ClientConstants;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.Sound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public abstract class flash_AbstractSoundInstanceMixin {
    @Shadow
    public abstract Sound getSound();

    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
    private void getVolume(CallbackInfoReturnable<Float> cir) {
        float volume = cir.getReturnValue();
        float flashStrength = ClientConstants.FLASH_RENDERER.getFlashStrength();
        if (flashStrength >= FlashRenderer.DECREASE_SOUND_BEGIN_STRENGTH) {
            volume -=  volume * Math.min(flashStrength - FlashRenderer.DECREASE_SOUND_BEGIN_STRENGTH, 0.9F);
        }
        cir.setReturnValue(volume);
    }
}
