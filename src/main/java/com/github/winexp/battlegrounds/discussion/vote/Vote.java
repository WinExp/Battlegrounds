package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.PlayerVotedPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.VoteClosedPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.VoteOpenedPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.VoteUpdatedPayloadS2C;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.task.ScheduledTask;
import com.github.winexp.battlegrounds.util.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.time.Duration;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.ValueLists;

import java.util.*;
import java.util.function.IntFunction;

public class Vote {
    public static final UUID EMPTY_UUID = UUID.nameUUIDFromBytes("Empty Vote".getBytes());
    public static final Vote EMPTY = new Vote(EMPTY_UUID, VotePresets.EMPTY, Collections.emptySet());
    public static final PacketCodec<RegistryByteBuf, Vote> PACKET_CODEC = new PacketCodec<>() {
        private static final PacketCodec<RegistryByteBuf, VoteInfo> VOTE_INFO_PACKET_CODEC = new PacketCodec<>() {
            @Override
            public VoteInfo decode(RegistryByteBuf buf) {
                if (buf.readBoolean()) {
                    return VotePreset.PACKET_CODEC.decode(buf);
                } else {
                    return VoteInfo.PACKET_CODEC.decode(buf);
                }
            }

            @Override
            public void encode(RegistryByteBuf buf, VoteInfo value) {
                if (value instanceof VotePreset votePreset) {
                    buf.writeBoolean(true);
                    VotePreset.PACKET_CODEC.encode(buf, votePreset);
                } else {
                    buf.writeBoolean(false);
                    VoteInfo.PACKET_CODEC.encode(buf, value);
                }
            }
        };

        @Override
        public Vote decode(RegistryByteBuf buf) {
            Vote vote = new Vote(
                    PacketByteBuf.readUuid(buf),
                    VOTE_INFO_PACKET_CODEC.decode(buf),
                    buf.readCollection(ObjectOpenHashSet::new, RegistryByteBuf::readUuid)
            );
            vote.locked = true;
            vote.expirationTask = new ScheduledTask(buf.readVarInt() + 200) {
                @Override
                public void run() {
                    vote.callback.onClosed(CloseReason.EXPIRED);
                }
            }.schedule();
            return vote;
        }

        @Override
        public void encode(RegistryByteBuf buf, Vote value) {
            PacketByteBuf.writeUuid(buf, value.uuid);
            VOTE_INFO_PACKET_CODEC.encode(buf, value.info);
            buf.writeCollection(value.participants, RegistryByteBuf::writeUuid);
            buf.writeVarInt(value.expirationTask.getDelayTicks());
        }
    };

    private final UUID uuid;
    private final VoteInfo info;
    private final Set<UUID> participants;
    private final Map<UUID, Boolean> results = Object2BooleanMaps.synchronize(new Object2BooleanOpenHashMap<>());
    private ScheduledTask expirationTask;
    private VoteCallback callback = VoteCallback.EMPTY;
    private boolean locked = false;

    private Vote(UUID uuid, VoteInfo info, Collection<UUID> participants) {
        if (uuid == null) {
            do {
                uuid = UUID.randomUUID();
            } while (VoteManager.INSTANCE.getVote(uuid) != null);
        }
        this.uuid = uuid;
        this.info = info;
        this.participants = new ObjectOpenHashSet<>(participants);
        if (uuid.equals(EMPTY_UUID) || info.getGroup().equals(EMPTY.getGroup())) {
            this.locked = true;
        }
    }

    public Vote(VoteInfo voteInfo) {
        this(null, voteInfo, Collections.emptySet());
    }

    public Vote(Identifier group, Text name, Text description, VoteMode voteMode) {
        this(new VoteInfo(group, name, description, voteMode));
    }

    public void addCallback(VoteCallback callback) {
        this.callback = this.callback.andThen(callback);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public VoteInfo getInfo() {
        return this.info;
    }

    public Identifier getGroup() {
        return this.info.getGroup();
    }

    public Text getName() {
        return this.info.getName();
    }

    public Text getDescription() {
        return this.info.getDescription();
    }

    public VoteMode getMode() {
        return this.info.getVoteMode();
    }

    public int getExpirationTicksRemaining() {
        return this.expirationTask.getDelayTicks();
    }

    public int getParticipantsCount() {
        return this.participants.size();
    }

    public int getAcceptedCount() {
        return this.countResult(true);
    }

    public int getDeniedCount() {
        return this.countResult(false);
    }

    private int countResult(boolean target) {
        int count = 0;
        for (boolean result : this.results.values()) {
            if (result == target) count++;
        }
        return count;
    }

    public Collection<UUID> getParticipants() {
        return this.participants;
    }

    public boolean isParticipant(UUID uuid) {
        return this.participants.contains(uuid);
    }

    public boolean isParticipant(PlayerEntity player) {
        return this.isParticipant(PlayerUtil.getAuthUUID(player));
    }

    public boolean isOpened() {
        return !this.locked && TaskScheduler.INSTANCE.isScheduled(this.expirationTask);
    }

    private void ensureIsOpened() {
        if (!this.isOpened()) {
            VoteManager.INSTANCE.removeVote(this.uuid);
            throw new IllegalStateException();
        }
    }

    public synchronized void open(Collection<UUID> participants, Duration timeoutDuration) {
        if (this.locked || this.isOpened()) throw new IllegalStateException();
        if (VoteManager.INSTANCE.isExceedsLimit()) {
            Constants.LOGGER.error("投票数量过多，已忽略 {}", this.uuid);
            return;
        }
        this.participants.addAll(participants);
        this.expirationTask = new ScheduledTask(timeoutDuration.getTicks()) {
            @Override
            public void run() {
                Vote.this.close(CloseReason.EXPIRED);
            }
        }.schedule();
        VoteManager.INSTANCE.putVote(this);
        PlayerUtil.broadcastPacket(this.participants, new VoteUpdatedPayloadS2C(this.uuid, Optional.of(this)));
        PlayerUtil.broadcastPacket(this.participants, new VoteOpenedPayloadS2C(this.uuid));
    }

    private synchronized void close(CloseReason closeReason) {
        boolean opened = this.isOpened();
        this.expirationTask.cancel();
        if (this.locked) return;
        if (!opened) throw new IllegalStateException();
        this.callback.onClosed(closeReason);
        this.locked = true;
        VoteManager.INSTANCE.removeVote(this.getUuid());
        PlayerUtil.broadcastPacket(this.participants, new VoteClosedPayloadS2C(this.uuid, closeReason));
        PlayerUtil.broadcastPacket(this.participants, new VoteUpdatedPayloadS2C(this.uuid, Optional.empty()));
    }

    public void close() {
        this.close(CloseReason.MANUAL);
    }

    public synchronized void vote(PlayerEntity player, boolean result) {
        this.ensureIsOpened();
        if (!this.isParticipant(player)) return;
        UUID uuid = PlayerUtil.getAuthUUID(player);
        this.results.put(uuid, result);
        PlayerUtil.broadcastPacket(this.participants, new PlayerVotedPayloadS2C(this.uuid, player.getDisplayName(), result));
        if (this.getMode().canTerminate(this)) this.close(CloseReason.TERMINATED);
        else if (this.getMode().canAccept(this)) this.close(CloseReason.ACCEPTED);
    }

    public enum CloseReason {
        ACCEPTED, TERMINATED, EXPIRED, MANUAL;

        private static final IntFunction<CloseReason> INDEX_TO_VALUE_INT_FUNCTION = ValueLists.createIdToValueFunction(CloseReason::ordinal, CloseReason.values(), ValueLists.OutOfBoundsHandling.WRAP);
        public static final PacketCodec<ByteBuf, CloseReason> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE_INT_FUNCTION, CloseReason::ordinal);
    }
}
