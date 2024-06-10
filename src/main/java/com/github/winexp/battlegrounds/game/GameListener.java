package com.github.winexp.battlegrounds.game;

import net.minecraft.server.network.ServerPlayerEntity;

public interface GameListener {
    void onTriggered(GameManager manager, GameTrigger gameTrigger);

    void onBorderResizing(GameManager manager);

    void onBorderResized(GameManager manager);

    void onPlayerWin(GameManager manager, ServerPlayerEntity player);

    void onTimeout(GameManager manager);
}
