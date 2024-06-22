package com.github.winexp.battlegrounds.client;

import com.github.winexp.battlegrounds.client.gui.screen.VoteScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class KeyBindings {
    private static final Map<KeyBinding, Consumer<MinecraftClient>> pressedActions = new HashMap<>();
    public static final KeyBinding VOTE_SCREEN = register(new KeyBinding(
            "key.battlegrounds.vote_screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "battlegrounds.name"
    ), client -> client.setScreen(new VoteScreen()));

    private static KeyBinding register(KeyBinding keyBinding, Consumer<MinecraftClient> pressedAction) {
        pressedActions.put(keyBinding, pressedAction);
        return KeyBindingHelper.registerKeyBinding(keyBinding);
    }

    public static void bootstrap() {
        pressedActions.forEach((keyBinding, action) ->
                ClientTickEvents.END_CLIENT_TICK.register(client -> {
                    while (keyBinding.wasPressed()) {
                        action.accept(client);
                    }
                }
        ));
    }
}
