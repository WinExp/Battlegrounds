package com.github.winexp.battlegrounds.config;

import com.github.winexp.battlegrounds.util.Constants;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.util.Identifier;

public class RootConfig {
    public static final ConfigClassHandler<RootConfig> HANDLER = ConfigClassHandler.createBuilder(RootConfig.class)
            .id(new Identifier("battlegrounds", "root_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(Constants.CONFIG_PATH.resolve("config.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public boolean debug = false;
}
