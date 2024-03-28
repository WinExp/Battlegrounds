package com.github.winexp.battlegrounds.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.BackgroundRenderer;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ClientApplyFogCallback {
    Event<ClientApplyFogCallback> EVENT = EventFactory.createArrayBacked(ClientApplyFogCallback.class,
            (listeners) -> (viewDistance, fogData) -> {
                for (ClientApplyFogCallback listener : listeners) {
                    listener.onApplyFog(viewDistance, fogData);
                }
            });

    void onApplyFog(float viewDistance, BackgroundRenderer.FogData fogData);
}
