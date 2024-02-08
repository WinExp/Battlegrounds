package com.github.winexp.battlegrounds.mixins;

import com.github.winexp.battlegrounds.events.server.ServerLoadedCallback;
import com.github.winexp.battlegrounds.events.server.ServerSavingCallback;
import com.github.winexp.battlegrounds.events.server.ServerTickCallback;
import com.github.winexp.battlegrounds.events.server.WorldsLoadedCallback;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void onServerLoaded(CallbackInfo ci) {
        ServerLoadedCallback.EVENT.invoker().interact((MinecraftServer) (Object) this);
    }

    @Inject(method = "loadWorld", at = @At("TAIL"))
    private void onWorldLoaded(CallbackInfo ci) {
        WorldsLoadedCallback.EVENT.invoker().interact((MinecraftServer) (Object) this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onServerTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ServerTickCallback.EVENT.invoker().interact((MinecraftServer) (Object) this);
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void onSaving(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        ServerSavingCallback.EVENT.invoker().interact((MinecraftServer) (Object) this, suppressLogs);
    }
}
