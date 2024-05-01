package com.github.winexp.battlegrounds.game;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface GameListener {
    void onStageTriggered(GameManager manager, @Nullable Identifier triggerId);

    void onBorderResizing(GameManager manager);

    void onBorderResized(GameManager manager);

    void onPlayerWin(GameManager manager, ServerPlayerEntity player);

    void onTimeout(GameManager manager);
}
