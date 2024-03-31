package com.github.winexp.battlegrounds.event;

import com.github.winexp.battlegrounds.game.GameManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ServerGameEvents {
    public static Event<StageTriggered> STAGE_TRIGGERED = EventFactory.createArrayBacked(StageTriggered.class,
            (listeners) -> (gameManager, id) -> {
                for (StageTriggered listener : listeners) {
                    listener.onTriggered(gameManager, id);
                }
            });
    public static Event<BorderResizing> BORDER_RESIZING = EventFactory.createArrayBacked(BorderResizing.class,
            (listeners) -> (gameManager) -> {
                for (BorderResizing listener : listeners) {
                    listener.onBorderResizing(gameManager);
                }
            });
    public static Event<BorderResizeCompleted> BORDER_RESIZE_COMPLETED = EventFactory.createArrayBacked(BorderResizeCompleted.class,
            (listeners) -> (gameManager) -> {
                for (BorderResizeCompleted listener : listeners) {
                    listener.onBorderResizeCompleted(gameManager);
                }
            });
    public static Event<PlayerWon> PLAYER_WON = EventFactory.createArrayBacked(PlayerWon.class,
            (listeners) -> (gameManager, player) -> {
                for (PlayerWon listener : listeners) {
                    listener.onPlayerWon(gameManager, player);
                }
            });
    public static Event<GameTie> GAME_TIE = EventFactory.createArrayBacked(GameTie.class,
            (listeners) -> (gameManager) -> {
                for (GameTie listener : listeners) {
                    listener.onGameTie(gameManager);
                }
            }
    );

    @FunctionalInterface
    public interface StageTriggered {
        void onTriggered(GameManager gameManager, @Nullable Identifier trigger);
    }

    @FunctionalInterface
    public interface BorderResizing {
        void onBorderResizing(GameManager gameManager);
    }

    @FunctionalInterface
    public interface BorderResizeCompleted {
        void onBorderResizeCompleted(GameManager gameManager);
    }

    @FunctionalInterface
    public interface PlayerWon {
        void onPlayerWon(GameManager gameManager, ServerPlayerEntity winner);
    }

    @FunctionalInterface
    public interface GameTie {
        void onGameTie(GameManager gameManager);
    }
}
