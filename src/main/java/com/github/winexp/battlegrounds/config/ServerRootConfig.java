package com.github.winexp.battlegrounds.config;

import com.github.winexp.battlegrounds.game.GameTrigger;
import com.github.winexp.battlegrounds.game.GameTriggers;
import com.github.winexp.battlegrounds.registry.ModRegistries;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;

public record ServerRootConfig(RandomTpConfig randomTp, boolean debug) implements IConfig<ServerRootConfig> {
    public static final Codec<ServerRootConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RandomTpConfig.CODEC.fieldOf("random_tp").forGetter(ServerRootConfig::randomTp),
            Codec.BOOL.fieldOf("debug").forGetter(ServerRootConfig::debug)
    ).apply(instance, ServerRootConfig::new));

    public static final ServerRootConfig DEFAULT_CONFIG = new ServerRootConfig(
            new RandomTpConfig(
                    Duration.withSeconds(30),
                    Duration.withSeconds(20),
                    Map.of(
                            GameTriggers.DEVELOP_BEGIN, Duration.withMinutes(2),
                            GameTriggers.DEATHMATCH_BEGIN, Duration.withMinutes(4)
                    )
            ),
            false
    );

    @Override
    public Codec<ServerRootConfig> getCodec() {
        return CODEC;
    }

    public record RandomTpConfig(Duration defaultCooldown, Duration damagedCooldown, Map<GameTrigger, Duration> cooldowns) {
        public static final Codec<RandomTpConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Duration.CODEC.fieldOf("default_cooldown").forGetter(RandomTpConfig::defaultCooldown),
                Duration.CODEC.fieldOf("damaged_cooldown").forGetter(RandomTpConfig::damagedCooldown),
                Codec.unboundedMap(ModRegistries.GAME_TRIGGER.getCodec(), Duration.CODEC).fieldOf("cooldowns").forGetter(RandomTpConfig::cooldowns)
        ).apply(instance, RandomTpConfig::new));
    }
}
