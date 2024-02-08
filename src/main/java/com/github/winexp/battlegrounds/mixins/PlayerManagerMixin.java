package com.github.winexp.battlegrounds.mixins;

import com.github.winexp.battlegrounds.events.player.PlayerJoinedGameCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Unique
    private final ThreadLocal<Text> cachedText = new ThreadLocal<>();

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        CallbackInfoReturnable<Text> cir = new CallbackInfoReturnable<>("onPlayerJoin", true, null);
        PlayerJoinedGameCallback.EVENT.invoker().interact(connection, player, clientData, cir);
        cachedText.set(cir.getReturnValue());
    }

    @ModifyArg(method = "onPlayerConnect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V",
                    ordinal = 0))
    private Text onJoinBroadcast(Text message) {
        if (cachedText.get() == null) return message;
        else return cachedText.get();
    }
}
