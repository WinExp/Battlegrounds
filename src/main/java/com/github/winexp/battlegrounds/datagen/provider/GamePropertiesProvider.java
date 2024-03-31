package com.github.winexp.battlegrounds.datagen.provider;

import com.github.winexp.battlegrounds.game.GameProperties;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class GamePropertiesProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;

    protected GamePropertiesProvider(FabricDataOutput output) {
        this.output = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "game_properties");
    }

    public abstract void generateGameProperties(Consumer<GameProperties> exporter);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        HashSet<Identifier> identifiers = new HashSet<>();
        HashSet<GameProperties> properties = new HashSet<>();
        this.generateGameProperties(properties::add);
        ArrayList<CompletableFuture<?>> futures = new ArrayList<>();
        for (GameProperties property : properties) {
            if (!identifiers.add(property.id())) {
                throw new IllegalStateException("Duplicate game properties " + property.id());
            }
            JsonObject json = Util.getResult(GameProperties.CODEC.encodeStart(JsonOps.INSTANCE, property), IllegalStateException::new).getAsJsonObject();
            futures.add(DataProvider.writeToPath(writer, json, this.getOutputPath(property)));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private Path getOutputPath(GameProperties properties) {
        return this.pathResolver.resolveJson(properties.id());
    }

    @Override
    public String getName() {
        return "Game Properties";
    }
}
