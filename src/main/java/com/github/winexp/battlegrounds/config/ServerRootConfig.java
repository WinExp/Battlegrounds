package com.github.winexp.battlegrounds.config;

import com.github.winexp.battlegrounds.util.time.Duration;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.Map;

public record ServerRootConfig(RandomTpConfig randomTp, Map<Identifier, VoteConfig> votes, boolean debug) implements IConfig<ServerRootConfig> {
    public static final Codec<ServerRootConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RandomTpConfig.CODEC.fieldOf("random_tp").forGetter(ServerRootConfig::randomTp),
            Codec.unboundedMap(Identifier.CODEC, VoteConfig.CODEC).fieldOf("votes").forGetter(ServerRootConfig::votes),
            Codec.BOOL.fieldOf("debug").forGetter(ServerRootConfig::debug)
    ).apply(instance, ServerRootConfig::new));

    public static final ServerRootConfig DEFAULT_CONFIG = new ServerRootConfig(
            new RandomTpConfig(
                    Duration.withSeconds(30),
                    Duration.withSeconds(20),
                    Map.of(
                            new Identifier("battlegrounds", "develop"), Duration.withMinutes(2),
                            new Identifier("battlegrounds", "deathmatch"), Duration.withMinutes(4)
                    )
            ),
            ImmutableMap.of(
                    new Identifier("battlegrounds", "start_game"),
                    new VoteConfig(
                            Duration.withSeconds(30)
                    )
            ),
            false
    );

    @Override
    public Codec<ServerRootConfig> getCodec() {
        return CODEC;
    }

    public record RandomTpConfig(Duration defaultCooldown, Duration damagedCooldown, Map<Identifier, Duration> cooldownMap) {
        public static final Codec<RandomTpConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Duration.CODEC.fieldOf("default_cooldown").forGetter(RandomTpConfig::defaultCooldown),
                Duration.CODEC.fieldOf("damaged_cooldown").forGetter(RandomTpConfig::damagedCooldown),
                Codec.unboundedMap(Identifier.CODEC, Duration.CODEC).fieldOf("cooldown_map").forGetter(RandomTpConfig::cooldownMap)
        ).apply(instance, RandomTpConfig::new));
    }

    public record VoteConfig(Duration timeout) {
        public static final Codec<VoteConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Duration.CODEC.fieldOf("timeout").forGetter(VoteConfig::timeout)
        ).apply(instance, VoteConfig::new));
    }
}