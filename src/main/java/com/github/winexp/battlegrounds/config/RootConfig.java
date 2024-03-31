package com.github.winexp.battlegrounds.config;

import com.github.winexp.battlegrounds.util.time.Duration;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.Map;

public record RootConfig(RandomTpConfig randomTp, Map<Identifier, VoteConfig> votes, boolean debug) implements IConfig<RootConfig> {
    public static final Codec<RootConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RandomTpConfig.CODEC.fieldOf("random_tp").forGetter(RootConfig::randomTp),
            Codec.unboundedMap(Identifier.CODEC, VoteConfig.CODEC).fieldOf("votes").forGetter(RootConfig::votes),
            Codec.BOOL.fieldOf("debug").forGetter(RootConfig::debug)
    ).apply(instance, RootConfig::new));

    public static final RootConfig DEFAULT_CONFIG = new RootConfig(
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
    public Codec<RootConfig> getCodec() {
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
