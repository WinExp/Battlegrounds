package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.registry.ModRegistries;
import com.github.winexp.battlegrounds.util.data.ModGameConfig;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Range;

import java.util.List;

public record GameProperties(Identifier id, List<StageInfo> stages, Duration timeout) {
    public static final Codec<GameProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(GameProperties::id),
            Codecs.nonEmptyList(StageInfo.CODEC.listOf()).fieldOf("stages").forGetter(GameProperties::stages),
            Duration.CODEC.fieldOf("timeout").forGetter(GameProperties::timeout)
    ).apply(instance, GameProperties::new));

    public record StageInfo(List<GameTrigger> triggers, int initialSize, int resizeBlocks,
                            @Range(from = 1, to = Integer.MAX_VALUE) int resizeCount, ResizeTimeInfo resizeTimeInfo,
                            ModGameConfig gameConfig, boolean allowRespawn) {
        public static final Codec<StageInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.withAlternative(ModRegistries.GAME_TRIGGER.getCodec().listOf(), ModRegistries.GAME_TRIGGER.getCodec(), List::of).fieldOf("triggers").forGetter(StageInfo::triggers),
                Codecs.POSITIVE_INT.fieldOf("initial_size").forGetter(StageInfo::initialSize),
                Codec.INT.fieldOf("decrement_blocks").forGetter(StageInfo::resizeBlocks),
                Codecs.POSITIVE_INT.fieldOf("total_amount").forGetter(StageInfo::resizeCount),
                ResizeTimeInfo.CODEC.fieldOf("time").forGetter(StageInfo::resizeTimeInfo),
                ModGameConfig.CODEC.fieldOf("game_config").forGetter(StageInfo::gameConfig),
                Codec.BOOL.fieldOf("allow_respawn").forGetter(StageInfo::allowRespawn)
        ).apply(instance, StageInfo::new));

        public record ResizeTimeInfo(Duration spendTime, Duration delayTime) {
            public static final Codec<ResizeTimeInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Duration.CODEC.fieldOf("spend_time").forGetter(ResizeTimeInfo::spendTime),
                    Duration.CODEC.fieldOf("delay_time").forGetter(ResizeTimeInfo::delayTime)
            ).apply(instance, ResizeTimeInfo::new));
        }
    }
}
