package com.github.winexp.battlegrounds.util.time;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;

import java.util.Objects;

public class Duration {
    public static final PacketCodec<ByteBuf, Duration> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public Duration decode(ByteBuf buf) {
            return new Duration(Unit.MILLI, VarInts.read(buf));
        }

        @Override
        public void encode(ByteBuf buf, Duration value) {
            VarInts.write(buf, value.millis);
        }
    };
    public static final Duration ZERO = new Duration(Unit.MILLI, 0);
    public static final Duration INFINITY = new Duration(Unit.MILLI, -1);

    private final int millis;

    public Duration(Unit unit, int times) {
        this.millis = times * unit.millisMultiplier;
    }

    public static Duration create(int hours, int minutes, int seconds, int ticks, int millis) {
        return new Duration(Unit.MILLI, millis + ticks * Unit.TICK.millisMultiplier + seconds * Unit.SECOND.millisMultiplier + minutes * Unit.MINUTE.millisMultiplier + hours * Unit.HOUR.millisMultiplier);
    }

    public int withUnit(Unit unit) {
        return this.isInfinity() ? -1 : this.millis / unit.millisMultiplier;
    }

    public int getHours() {
        return this.withUnit(Unit.HOUR);
    }

    public int getMinutes() {
        return this.withUnit(Unit.MINUTE);
    }

    public int getSeconds() {
        return this.withUnit(Unit.SECOND);
    }

    public int getTicks() {
        return this.withUnit(Unit.TICK);
    }

    public int getMillis() {
        return this.withUnit(Unit.MILLI);
    }

    public boolean isInfinity() {
        return this.millis < 0;
    }

    public static Duration ofHours(int hours) {
        return new Duration(Unit.HOUR, hours);
    }
    public static Duration ofMinutes(int minutes) {
        return new Duration(Unit.MINUTE, minutes);
    }
    public static Duration ofSeconds(int seconds) {
        return new Duration(Unit.SECOND, seconds);
    }
    public static Duration ofTicks(int ticks) {
        return new Duration(Unit.TICK, ticks);
    }
    public static Duration ofMillis(int millis) {
        return new Duration(Unit.MILLI, millis);
    }

    public Duration add(Unit unit, int times) {
        return new Duration(Unit.MILLI, this.millis + times * unit.millisMultiplier);
    }

    public Duration add(Duration duration) {
        return new Duration(Unit.MILLI, this.millis + duration.millis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Duration duration)) return false;
        return this.millis == duration.millis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.millis);
    }

    public enum Unit {
        HOUR(60 * 60 * 1000),
        MINUTE(60 * 1000),
        SECOND(1000),
        TICK(50),
        MILLI(1);

        private final int millisMultiplier;

        Unit(int millisMultiplier) {
            this.millisMultiplier = millisMultiplier;
        }

        public int getMillisMultiplier() {
            return this.millisMultiplier;
        }
    }
}
