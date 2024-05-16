package com.github.winexp.battlegrounds.util.time;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.util.dynamic.Codecs;

public record Duration(int hours, int minutes, int seconds, int ticks, int millis) {
    public static final Codec<Duration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.POSITIVE_INT.optionalFieldOf("hours", 0).forGetter(Duration::hours),
            Codecs.POSITIVE_INT.optionalFieldOf("minutes", 0).forGetter(Duration::minutes),
            Codecs.POSITIVE_INT.optionalFieldOf("seconds", 0).forGetter(Duration::seconds),
            Codecs.POSITIVE_INT.optionalFieldOf("ticks", 0).forGetter(Duration::ticks),
            Codecs.POSITIVE_INT.optionalFieldOf("millis", 0).forGetter(Duration::millis)
    ).apply(instance, (hours, minutes, seconds, ticks, millis) ->
            new Duration(hours, minutes, seconds, ticks, millis).fix()));
    public static final PacketCodec<ByteBuf, Duration> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public Duration decode(ByteBuf buf) {
            return new Duration(VarInts.read(buf), VarInts.read(buf), VarInts.read(buf), VarInts.read(buf), VarInts.read(buf));
        }

        @Override
        public void encode(ByteBuf buf, Duration value) {
            value = Duration.fix(value);
            VarInts.write(buf, value.hours);
            VarInts.write(buf, value.minutes);
            VarInts.write(buf, value.seconds);
            VarInts.write(buf, value.ticks);
            VarInts.write(buf, value.millis);
        }
    };
    public static final Duration ZERO = new Duration(0, 0, 0, 0, 0);
    public static final Duration INFINITY = new Duration(-1, -1, -1, -1, -1);

    public static Duration fix(Duration duration) {
        int notFixedMillis = duration.toMillis();
        int millis = notFixedMillis % 1000;
        int ticks = notFixedMillis / 50 % 20;
        int seconds = notFixedMillis / 1000 % 60;
        int minutes = notFixedMillis / 1000 / 60 % 60;
        int hours = notFixedMillis / 1000 / 60 / 60;
        return new Duration(hours, minutes, seconds, ticks, millis);
    }

    public Duration fix() {
        return fix(this);
    }

    public static Duration withHours(int hours) {
        return new Duration(hours, 0, 0, 0, 0).fix();
    }
    public static Duration withMinutes(int minutes) {
        return new Duration(0, minutes, 0, 0, 0).fix();
    }
    public static Duration withSeconds(int seconds) {
        return new Duration(0, 0, seconds, 0, 0).fix();
    }
    public static Duration withTicks(int ticks) {
        return new Duration(0, 0, 0, ticks, 0).fix();
    }
    public static Duration withMillis(int millis) {
        return new Duration(0, 0, 0, 0, millis).fix();
    }

    public int toHours() {
        return this.toMillis() / 60;
    }
    public int toMinutes() {
        return this.toSeconds() / 60;
    }
    public int toSeconds() {
        return this.toMillis() / 1000;
    }
    public int toTicks() {
        return this.toMillis() / 50;
    }
    public int toMillis() {
        return this.hours * 60 * 60 * 1000
                + this.minutes * 60 * 1000
                + this.seconds * 1000
                + this.ticks * 50
                + this.millis;
    }

    public Duration add(Duration another) {
        return new Duration(this.hours + another.hours, this.minutes + another.minutes,
                this.seconds + another.seconds, this.ticks + another.ticks, this.millis + another.millis).fix();
    }
}
