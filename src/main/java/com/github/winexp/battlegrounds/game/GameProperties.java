package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.util.time.Duration;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Range;

import java.util.List;

public record GameProperties(Identifier id, List<StageInfo> stages) {
    public static final Codec<GameProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(GameProperties::id),
            StageInfo.CODEC.listOf().fieldOf("stages").forGetter(GameProperties::stages)
    ).apply(instance, GameProperties::new));

    public static final GameProperties NORMAL_PRESET = new GameProperties(new Identifier("battlegrounds", "normal"), ImmutableList.of(
            new StageInfo(
                    new Identifier("battlegrounds", "develop"), 5000, 500, 2,
                    new StageInfo.TimeInfo(
                            Duration.withMinutes(4),
                            Duration.withMinutes(3)
                    )
            ),
            new StageInfo(
                    new Identifier("battlegrounds", "enable_pvp"), 4000, 500, 2,
                    new StageInfo.TimeInfo(
                            Duration.withMinutes(4),
                            Duration.withMinutes(3)
                    )
            ),
            new StageInfo(
                    new Identifier("battlegrounds", "deathmatch"), 4000, 500, 2,
                    new StageInfo.TimeInfo(
                            Duration.withMinutes(4),
                            Duration.withMinutes(3)
                    )
            )
    ));

    public record StageInfo(Identifier id, int initialSize, int resizeBlocks, @Range(from = 1, to = Integer.MAX_VALUE) int resizeCount, TimeInfo timeInfo) {
        public static final Codec<StageInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(StageInfo::id),
                Codec.INT.fieldOf("initial_size").forGetter(StageInfo::initialSize),
                Codec.INT.fieldOf("resize_blocks").forGetter(StageInfo::resizeBlocks),
                Codec.INT.fieldOf("total_amount").forGetter(StageInfo::resizeCount),
                TimeInfo.CODEC.fieldOf("time").forGetter(StageInfo::timeInfo)
        ).apply(instance, StageInfo::new));

        public record TimeInfo(Duration spendTime, Duration delayTime) {
            public static final Codec<TimeInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Duration.CODEC.fieldOf("spend_time").forGetter(TimeInfo::spendTime),
                    Duration.CODEC.fieldOf("delay_time").forGetter(TimeInfo::delayTime)
            ).apply(instance, TimeInfo::new));
        }
    }
}
