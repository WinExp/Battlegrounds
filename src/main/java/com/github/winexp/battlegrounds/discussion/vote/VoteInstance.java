package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.client.gui.screen.vote.VoteScreen;
import com.github.winexp.battlegrounds.event.ServerVoteEvents;
import com.github.winexp.battlegrounds.util.task.ScheduledTask;
import com.github.winexp.battlegrounds.util.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;

public class VoteInstance {
    public static final PacketCodec<ByteBuf, VoteInstance> PACKET_CODEC = new PacketCodec<>() {
        private static final PacketCodec<ByteBuf, UUID> UUID_PACKET_CODEC = PacketCodec.ofStatic(PacketByteBuf::writeUuid, PacketByteBuf::readUuid);
        private static final PacketCodec<ByteBuf, List<UUID>> UUID_LIST_PACKET_CODEC = PacketCodecs.<ByteBuf, UUID>toList().apply(UUID_PACKET_CODEC);

        @Override
        public VoteInstance decode(ByteBuf buf) {
            return Util.make(new VoteInstance(
                    Identifier.PACKET_CODEC.decode(buf),
                    PacketByteBuf.readUuid(buf),
                    TextCodecs.PACKET_CODEC.decode(buf),
                    TextCodecs.PACKET_CODEC.decode(buf),
                    VoteSettings.PACKET_CODEC.decode(buf),
                    TextCodecs.PACKET_CODEC.decode(buf),
                    false
            ), voteInstance -> {
                voteInstance.voting = buf.readBoolean();
                // 后处理
                voteInstance.timeoutTask = new ScheduledTask(Duration.PACKET_CODEC.decode(buf)) {
                    @Override
                    public void run() throws CancellationException {
                        try {
                            VoteScreen.onVoteExpired(voteInstance);
                        } catch (Exception ignored) {
                        }
                    }
                };
                TaskScheduler.INSTANCE.schedule(voteInstance.timeoutTask);
                voteInstance.participants = UUID_LIST_PACKET_CODEC.decode(buf);
            });
        }

        @Override
        public void encode(ByteBuf buf, VoteInstance value) {
            Identifier.PACKET_CODEC.encode(buf, value.identifier);
            PacketByteBuf.writeUuid(buf, value.uuid);
            TextCodecs.PACKET_CODEC.encode(buf, value.name);
            TextCodecs.PACKET_CODEC.encode(buf, value.description);
            VoteSettings.PACKET_CODEC.encode(buf, value.settings);
            TextCodecs.PACKET_CODEC.encode(buf, value.initiatorName);

            // 后处理
            buf.writeBoolean(value.voting);
            Duration.PACKET_CODEC.encode(buf, Duration.withTicks(value.timeoutTask.getDelayTicks()));
            UUID_LIST_PACKET_CODEC.encode(buf, value.participants);
        }
    };

    private final Identifier identifier;
    private final UUID uuid;
    private final Text name;
    private final Text description;
    private final VoteSettings settings;
    private final Text initiatorName;
    private final boolean mutable;
    private boolean voting = false;
    private ScheduledTask timeoutTask = ScheduledTask.NONE_TASK;
    private List<UUID> participants = ImmutableList.of();
    private VoteCallback callback = VoteCallback.EMPTY;
    private final Object lock = new Object();
    private final Map<UUID, Boolean> voteResultMap = new ConcurrentHashMap<>();
    private final Map<String, Object> parameters = new ConcurrentHashMap<>();

    private VoteInstance(Identifier identifier, UUID uuid, Text name, Text description, VoteSettings settings, Text initiatorName, boolean mutable) {
        this.identifier = identifier;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.settings = settings;
        this.initiatorName = initiatorName;
        this.mutable = mutable;
    }

    public VoteInstance(Identifier identifier, Text name, Text description, VoteSettings settings, Text initiatorName) {
        this(identifier, UUID.randomUUID(), name, description, settings, initiatorName, true);
    }

    public static VoteInstance createWithPreset(VotePreset preset, Text initiatorName) {
        return new VoteInstance(preset.identifier(), preset.name().orElse(Text.translatable(preset.getTranslationKey("title"))),
                preset.description().orElse(Text.translatable(preset.getTranslationKey("description"))), preset.voteSettings(), initiatorName)
                .callback(preset.callback().orElse(null));
    }

    @Contract(value = "_ -> this", pure = true)
    public VoteInstance callback(VoteCallback callback) {
        this.ensureIsMutable();
        this.callback = callback;
        return this;
    }

    public Object getParameter(String key) {
        return this.parameters.get(key);
    }

    public void putParameter(String key, Object parameter) {
        this.parameters.put(key, parameter);
    }

    private void ensureIsMutable() {
        if (!this.mutable) throw new IllegalStateException();
    }

    public int getTimeLeft() {
        if (this.settings.timeout() == Duration.INFINITY) {
            return -1;
        } else if (this.timeoutTask.getDelayTicks() == -1 || this.timeoutTask.isCancelled()) {
            return 0;
        } else return this.timeoutTask.getDelayTicks();
    }

    public boolean isAvailableForPlayer(UUID uuid) {
        return this.voting && (this.settings.allowChangeVote() || !this.isVoted(uuid)) && this.isParticipant(uuid);
    }

    public boolean isAvailableForPlayer(PlayerEntity player) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        return this.isAvailableForPlayer(uuid);
    }

    public boolean isVoting() {
        return this.voting;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public Text getName() {
        return this.name;
    }

    public Text getDescription() {
        return this.description;
    }

    public int getTotal() {
        return this.voteResultMap.size();
    }

    public UUID getUuid() {
        return uuid;
    }

    public VoteSettings getSettings() {
        return settings;
    }

    public Text getInitiatorName() {
        return this.initiatorName;
    }

    public Collection<UUID> getParticipants() {
        return this.participants;
    }

    public int getAcceptedNum() {
        int num = 0;
        for (boolean accepted : this.voteResultMap.values()) {
            if (accepted) num++;
        }
        return num;
    }

    public boolean isParticipant(ServerPlayerEntity player) {
        return this.isParticipant(PlayerUtil.getAuthUUID(player));
    }

    public boolean isParticipant(UUID uuid) {
        return this.participants.contains(uuid);
    }

    public boolean isVoted(ServerPlayerEntity player) {
        return this.isVoted(PlayerUtil.getAuthUUID(player));
    }

    public boolean isVoted(UUID uuid) {
        return this.voteResultMap.containsKey(uuid);
    }
    
    public boolean acceptVote(ServerPlayerEntity player) {
        this.ensureIsMutable();
        synchronized (this.lock) {
            UUID uuid = PlayerUtil.getAuthUUID(player);
            if (!this.voting) return false;
            if (!this.isParticipant(player)) return false;
            if (!this.settings.allowChangeVote() && this.isVoted(uuid)) return false;
            this.voteResultMap.put(uuid, true);
            this.callback.onPlayerVoted(this, player, true);
            ServerVoteEvents.PLAYER_VOTED.invoker().onPlayerVoted(player, this, true);
            if (this.settings.voteMode().acceptPredicate.test(this.participants.size(), this.getAcceptedNum())) {
                this.closeVote(CloseReason.ACCEPTED);
            }
            return true;
        }
    }

    public boolean denyVote(ServerPlayerEntity player) {
        this.ensureIsMutable();
        synchronized (this.lock) {
            UUID uuid = PlayerUtil.getAuthUUID(player);
            if (!this.voting) return false;
            if (!this.isParticipant(player)) return false;
            if (!this.settings.allowChangeVote() && this.isVoted(uuid)) return false;
            if (this.settings.voteMode().canDenyCancel) this.closeVote(CloseReason.DENIED);
            this.voteResultMap.put(uuid, false);
            this.callback.onPlayerVoted(this, player, false);
            ServerVoteEvents.PLAYER_VOTED.invoker().onPlayerVoted(player, this, false);
            return true;
        }
    }

    public boolean openVote(Collection<ServerPlayerEntity> participants) {
        this.ensureIsMutable();
        synchronized (this.lock) {
            if (this.voting) return false;
            this.voteResultMap.clear();
            this.participants = ImmutableList.copyOf(participants.stream().map(PlayerUtil::getAuthUUID).toList());
            if (this.settings.timeout() != Duration.INFINITY) {
                this.timeoutTask = new ScheduledTask(this.settings.timeout()) {
                    @Override
                    public void run() throws CancellationException {
                        VoteInstance.this.closeVote(CloseReason.TIMEOUT);
                    }
                };
                TaskScheduler.INSTANCE.schedule(this.timeoutTask);
            }
            this.voting = true;
            ServerVoteEvents.OPENED.invoker().onOpened(this);
            return true;
        }
    }

    public boolean closeVote(CloseReason closeReason) {
        this.ensureIsMutable();
        synchronized (this.lock) {
            if (!this.voting) return false;
            this.timeoutTask.cancel();
            this.voting = false;
            this.callback.onClosed(this, closeReason);
            ServerVoteEvents.CLOSED.invoker().onClosed(this, closeReason);
            return true;
        }
    }

    @Override
    public int hashCode() {
        return 31 * this.identifier.hashCode() + this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VoteInstance voteInstance)) return false;
        else if (!this.identifier.equals(voteInstance.identifier)) return false;
        else return this.uuid.equals(voteInstance.uuid);
    }
}
