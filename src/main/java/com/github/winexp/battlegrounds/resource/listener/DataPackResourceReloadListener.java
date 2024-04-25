package com.github.winexp.battlegrounds.resource.listener;

import com.github.winexp.battlegrounds.game.GameProperties;
import com.github.winexp.battlegrounds.util.Constants;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;

public class DataPackResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID = new Identifier("battlegrounds", "game_properties");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        Constants.GAME_PROPERTIES.clear();
        for (Resource resource : manager.findResources("game_properties", identifier ->
                identifier.getPath().endsWith(".json")).values()) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader reader = resource.getReader()) {
                    while (reader.ready()) {
                        stringBuilder.append(reader.readLine());
                        stringBuilder.append(System.lineSeparator());
                    }
                }
                JsonElement json = Constants.GSON.fromJson(stringBuilder.toString(), JsonElement.class);
                GameProperties gameProperties = GameProperties.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                Constants.GAME_PROPERTIES.put(gameProperties.id(), gameProperties);
            } catch (Exception e) {
                Constants.LOGGER.error("Unable to load game properties: ", e);
            }
        }
    }
}
