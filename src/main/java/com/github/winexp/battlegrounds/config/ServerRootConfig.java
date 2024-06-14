package com.github.winexp.battlegrounds.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ServerRootConfig(boolean debug) implements IConfig<ServerRootConfig> {
    public static final Codec<ServerRootConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("debug").forGetter(ServerRootConfig::debug)
    ).apply(instance, ServerRootConfig::new));

    public static final ServerRootConfig DEFAULT_CONFIG = new ServerRootConfig(
            false
    );

    @Override
    public Codec<ServerRootConfig> getCodec() {
        return CODEC;
    }
}
