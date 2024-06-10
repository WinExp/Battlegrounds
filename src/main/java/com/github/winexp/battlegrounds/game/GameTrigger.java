package com.github.winexp.battlegrounds.game;

@FunctionalInterface
public interface GameTrigger {
    void apply(GameManager gameManager);
}
