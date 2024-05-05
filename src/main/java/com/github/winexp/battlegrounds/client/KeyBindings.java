package com.github.winexp.battlegrounds.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeyBindings {
    public static final KeyBinding VOTE_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.battlegrounds.vote_screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.battlegrounds"
    ));

    public static void bootstrap() {
    }
}
