package com.github.winexp.battlegrounds.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.Item;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;
import java.util.function.Function;

public record SoakComponent(ObjectArrayList<StatusEffectInstance> immerseEffects, ObjectArrayList<StatusEffectInstance> leachEffects, boolean showInTooltip) implements TooltipAppender {
    public static final Codec<SoakComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(StatusEffectInstance.CODEC).xmap(ObjectArrayList::new, Function.identity()).fieldOf("immerse_effects").forGetter(SoakComponent::immerseEffects),
            Codec.list(StatusEffectInstance.CODEC).xmap(ObjectArrayList::new, Function.identity()).fieldOf("leach_effects").forGetter(SoakComponent::leachEffects),
            Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(SoakComponent::showInTooltip)
    ).apply(instance, SoakComponent::new));
    private static final PacketCodec<RegistryByteBuf, ObjectArrayList<StatusEffectInstance>> LIST_PACKET_CODEC = PacketCodecs.<RegistryByteBuf, StatusEffectInstance>toList().apply(StatusEffectInstance.PACKET_CODEC).xmap(ObjectArrayList::new, Function.identity());
    public static final PacketCodec<RegistryByteBuf, SoakComponent> PACKET_CODEC = PacketCodec.tuple(LIST_PACKET_CODEC, SoakComponent::immerseEffects, LIST_PACKET_CODEC, SoakComponent::leachEffects, PacketCodecs.BOOL, SoakComponent::showInTooltip, SoakComponent::new);

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (!this.showInTooltip) return;
        if (!this.immerseEffects.isEmpty()) tooltip.accept(Text.translatable("soak.battlegrounds.immerse.tooltip").formatted(Formatting.WHITE));
        this.immerseEffects.forEach(effect -> addEffectTooltip(tooltip, context, effect));
        if (!this.leachEffects.isEmpty()) tooltip.accept(Text.translatable("soak.battlegrounds.leach.tooltip").formatted(Formatting.WHITE));
        this.leachEffects.forEach(effect -> addEffectTooltip(tooltip, context, effect));
    }

    private static void addEffectTooltip(Consumer<Text> appender, Item.TooltipContext context, StatusEffectInstance instance) {
        appender.accept(Text.literal("    "));
        appender.accept(Text.translatable(instance.getTranslationKey()).formatted(Formatting.GRAY)
                .append(Text.translatable("enchantment.level." + instance.getAmplifier() + 1))
                .append(Text.literal(": "))
                .append(StatusEffectUtil.getDurationText(instance, 1.0F, context.getUpdateTickRate())));
    }
}
