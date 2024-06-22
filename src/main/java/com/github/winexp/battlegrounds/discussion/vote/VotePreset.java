package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.registry.ModRegistries;
import com.github.winexp.battlegrounds.registry.ModRegistryKeys;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class VotePreset extends VoteInfo {
    public static final PacketCodec<RegistryByteBuf, VotePreset> PACKET_CODEC = PacketCodec.recursive(codec -> PacketCodecs.registryValue(ModRegistryKeys.VOTE_PRESET));

    public VotePreset(Identifier group, Text name, Text description, VoteMode voteMode) {
        super(group, name, description, voteMode);
    }

    @Nullable
    public static Identifier getIdentifier(VoteInfo voteInfo) {
        try {
            return ModRegistries.VOTE_PRESET.getId((VotePreset) voteInfo);
        } catch (Exception e) {
            return null;
        }
    }

    public Identifier getIdentifier() {
        return getIdentifier(this);
    }
}
