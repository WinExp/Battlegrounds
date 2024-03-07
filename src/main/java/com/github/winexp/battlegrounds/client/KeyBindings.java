package com.github.winexp.battlegrounds.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyBinding VOTE_SCREEN;

    public static void registerKeyBindings() {
        VOTE_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.battlegrounds.vote_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key.categories.battlegrounds"
        ));
    }
}
