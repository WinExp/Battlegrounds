package com.github.winexp.battlegrounds.event;

import com.github.winexp.battlegrounds.game.GameProperties;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ServerGameEvents {
    public static Event<StageChanged> STAGE_CHANGED = EventFactory.createArrayBacked(StageChanged.class,
            (listeners) -> (id) -> {
                for (StageChanged listener : listeners) {
                    listener.onStageChanged(id);
                }
            });
    public static Event<BorderResizing> BORDER_RESIZING = EventFactory.createArrayBacked(BorderResizing.class,
            (listeners) -> (currentStage, resizeCount) -> {
                for (BorderResizing listener : listeners) {
                    listener.onBorderResizing(currentStage, resizeCount);
                }
            });
    public static Event<PlayerWon> PLAYER_WON = EventFactory.createArrayBacked(PlayerWon.class,
            (listeners) -> (player) -> {
                for (PlayerWon listener : listeners) {
                    listener.onPlayerWon(player);
                }
            });

    @FunctionalInterface
    public interface StageChanged {
        void onStageChanged(@Nullable Identifier id);
    }

    @FunctionalInterface
    public interface BorderResizing {
        void onBorderResizing(GameProperties.StageInfo currentStage, int resizeCount);
    }

    @FunctionalInterface
    public interface PlayerWon {
        void onPlayerWon(ServerPlayerEntity winner);
    }
}
