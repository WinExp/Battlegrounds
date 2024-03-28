package com.github.winexp.battlegrounds.util.time;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Duration(int hours, int minutes, int seconds, int ticks, int millis) {
    public static final Codec<Duration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("hours", 0).forGetter(Duration::hours),
            Codec.INT.optionalFieldOf("minutes", 0).forGetter(Duration::minutes),
            Codec.INT.optionalFieldOf("seconds", 0).forGetter(Duration::seconds),
            Codec.INT.optionalFieldOf("ticks", 0).forGetter(Duration::ticks),
            Codec.INT.optionalFieldOf("millis", 0).forGetter(Duration::millis)
    ).apply(instance, Duration::new));
    public static final Duration ZERO = new Duration(0, 0, 0, 0, 0);
    public static final Duration INFINITY = new Duration(-1, -1, -1, -1, -1);

    public static Duration withHours(int hours) {
        return new Duration(hours, 0, 0, 0, 0);
    }

    public static Duration withMinutes(int minutes) {
        return new Duration(0, minutes, 0, 0, 0);
    }

    public static Duration withSeconds(int seconds) {
        return new Duration(0, 0, seconds, 0, 0);
    }

    public static Duration withTicks(int ticks) {
        return new Duration(0, 0, 0, ticks, 0);
    }

    public static Duration withMillis(int millis) {
        return new Duration(0, 0, 0, 0, millis);
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
                this.seconds + another.seconds, this.ticks + another.ticks, this.millis + another.millis);
    }
}
