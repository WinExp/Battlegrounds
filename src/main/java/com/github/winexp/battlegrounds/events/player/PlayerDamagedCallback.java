package com.github.winexp.battlegrounds.events.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerDamagedCallback {
    Event<PlayerDamagedCallback> EVENT = EventFactory.createArrayBacked(PlayerDamagedCallback.class,
            (listeners) -> (source, instance) -> {
                for (PlayerDamagedCallback listener : listeners){
                    ActionResult actionResult = listener.interact(source, instance);
                    if (actionResult != ActionResult.PASS) return actionResult;
                }

                return ActionResult.PASS;
            });

    ActionResult interact(DamageSource source, ServerPlayerEntity instance);
}
