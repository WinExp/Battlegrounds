package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.util.time.Duration;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

import java.util.function.BiPredicate;
import java.util.function.IntFunction;

public record VoteSettings(VoteMode voteMode, Duration timeout, boolean allowChangeVote) {
    public static PacketCodec<ByteBuf, VoteSettings> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public VoteSettings decode(ByteBuf buf) {
            return new VoteSettings(VoteMode.PACKET_CODEC.decode(buf), Duration.PACKET_CODEC.decode(buf), buf.readBoolean());
        }

        @Override
        public void encode(ByteBuf buf, VoteSettings value) {
            VoteMode.PACKET_CODEC.encode(buf, value.voteMode);
            Duration.PACKET_CODEC.encode(buf, value.timeout);
            buf.writeBoolean(value.allowChangeVote);
        }
    };

    public enum VoteMode {
        ALL_ACCEPT(true, Integer::equals),
        OVER_HALF_ACCEPT(false, (total, current) -> (double) current / total > 0.5);

        public static final IntFunction<VoteMode> INT_TO_VALUE_FUNCTION = ValueLists.createIdToValueFunction(VoteMode::ordinal, VoteMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
        public static final PacketCodec<ByteBuf, VoteMode> PACKET_CODEC = PacketCodecs.indexed(INT_TO_VALUE_FUNCTION, VoteMode::ordinal);

        public final boolean canDenyCancel;
        public final BiPredicate<Integer, Integer> acceptPredicate;

        VoteMode(boolean canDenyCancel, BiPredicate<Integer, Integer> acceptPredicate) {
            this.canDenyCancel = canDenyCancel;
            this.acceptPredicate = acceptPredicate;
        }
    }
}
