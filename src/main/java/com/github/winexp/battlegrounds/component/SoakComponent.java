package com.github.winexp.battlegrounds.component;

import com.github.winexp.battlegrounds.mixin.StatusEffectInstanceInvoker;
import com.github.winexp.battlegrounds.mixin.StatusEffectInstanceParametersInvoker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.Item;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableDouble;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public record SoakComponent(SoakType soakType, Object2ObjectOpenHashMap<RegistryEntry<StatusEffect>, EffectEntry> effects, boolean showInTooltip) implements TooltipAppender {
    public static final Codec<SoakComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SoakType.CODEC.fieldOf("soak_type").forGetter(SoakComponent::soakType),
            Codec.unboundedMap(Registries.STATUS_EFFECT.getEntryCodec(), EffectEntry.CODEC).xmap(Object2ObjectOpenHashMap::new, Function.identity()).fieldOf("effects").forGetter(SoakComponent::effects),
            Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(SoakComponent::showInTooltip)
    ).apply(instance, SoakComponent::new));
    private static final PacketCodec<RegistryByteBuf, Object2ObjectOpenHashMap<RegistryEntry<StatusEffect>, EffectEntry>> EFFECTS_PACKET_CODEC = PacketCodecs.map(Object2ObjectOpenHashMap::new, PacketCodecs.registryEntry(RegistryKeys.STATUS_EFFECT), EffectEntry.PACKET_CODEC);
    public static final PacketCodec<RegistryByteBuf, SoakComponent> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public SoakComponent decode(RegistryByteBuf buf) {
            return new SoakComponent(SoakType.PACKET_CODEC.decode(buf), EFFECTS_PACKET_CODEC.decode(buf), buf.readBoolean());
        }

        @Override
        public void encode(RegistryByteBuf buf, SoakComponent value) {
            SoakType.PACKET_CODEC.encode(buf, value.soakType);
            EFFECTS_PACKET_CODEC.encode(buf, value.effects);
            buf.writeBoolean(value.showInTooltip);
        }
    };

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (!this.showInTooltip) return;
        if (!this.effects.isEmpty()) tooltip.accept(this.soakType.tooltipText);
        this.effects.forEach((effect, entry) -> entry.appendTooltip(context, tooltip, this.soakType, effect));
    }

    public boolean isValid(RegistryEntry<StatusEffect> effect) {
        if (this.effects.containsKey(effect)) {
            return this.effects.get(effect).isValid();
        } else return false;
    }

    public boolean contains(StatusEffectInstance instance) {
        instance = new StatusEffectInstance(instance);
        RegistryEntry<StatusEffect> effect = instance.getEffectType();
        if (this.effects.containsKey(effect)) {
            StatusEffectInstance existingInstance = this.effects.get(effect).toStatusEffectInstance(effect);
            return !existingInstance.upgrade(instance);
        } else return false;
    }

    public record EffectEntry(StatusEffectInstance.Parameters originParameters, MutableDouble durationDecrement) {
        private static final Codec<Double> NON_NEGATIVE_DOUBLE_CODEC = Codec.DOUBLE.validate(d -> d >= 0 ? DataResult.success(d) : DataResult.error(() -> "Requires a non-negative double value"));
        public static final Codec<EffectEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                StatusEffectInstance.Parameters.CODEC.codec().fieldOf("origin_parameters").forGetter(EffectEntry::originParameters),
                NON_NEGATIVE_DOUBLE_CODEC.xmap(MutableDouble::new, MutableDouble::getValue).fieldOf("duration_decrement").forGetter(EffectEntry::durationDecrement)
        ).apply(instance, EffectEntry::new));
        public static final PacketCodec<ByteBuf, EffectEntry> PACKET_CODEC = new PacketCodec<>() {
            @Override
            public EffectEntry decode(ByteBuf buf) {
                return new EffectEntry(StatusEffectInstance.Parameters.PACKET_CODEC.decode(buf), new MutableDouble(buf.readDouble()));
            }

            @Override
            public void encode(ByteBuf buf, EffectEntry value) {
                StatusEffectInstance.Parameters.PACKET_CODEC.encode(buf, value.originParameters);
                buf.writeDouble(value.durationDecrement.getValue());
            }
        };

        public void appendTooltip(Item.TooltipContext context, Consumer<Text> appender, SoakType soakType, RegistryEntry<StatusEffect> effect) {
            StatusEffectInstance.Parameters parameters = this.getActuallyParameters();
            double remainingSeconds = (this.originParameters.duration() - this.durationDecrement.getValue()) / soakType.durationDecrementAddPerTick / context.getUpdateTickRate();
            float tickRate = context.getUpdateTickRate();
            appender.accept(Text.literal("  ").formatted(Formatting.GRAY)
                    .append(Text.translatable(effect.value().getTranslationKey()))
                    .append(ScreenTexts.SPACE)
                    .append(Text.translatable("enchantment.level." + (parameters.amplifier() + 1)))
                    .append(ScreenTexts.SPACE)
                    .append(StatusEffectUtil.getDurationText(this.toStatusEffectInstance(effect), 1.0F, tickRate))
                    .append(ScreenTexts.SPACE)
                    .append(Text.translatable("soak.battlegrounds.tooltip.remaining", (int) remainingSeconds))
            );
        }

        public EffectEntry withAmplifierMultiplierAndDurationDecrement(double amplifierMultiplier, double durationDecrement) {
            durationDecrement = Math.min(durationDecrement, this.originParameters.duration());
            StatusEffectInstance.Parameters parameters = StatusEffectInstanceParametersInvoker.invokeInit((int) Math.round(this.originParameters.amplifier() * amplifierMultiplier), (int) (this.originParameters.duration() - durationDecrement),
                    this.originParameters.ambient(), this.originParameters.showParticles(), this.originParameters.showIcon(), this.originParameters.hiddenEffect());
            return new EffectEntry(parameters, this.durationDecrement);
        }

        public boolean isValid() {
            return this.getActuallyParameters().duration() > 0;
        }

        public StatusEffectInstance.Parameters getActuallyParameters() {
            double percentage = (this.originParameters.duration() - this.durationDecrement.getValue()) / this.originParameters.duration();
            return this.withAmplifierMultiplierAndDurationDecrement(percentage, this.durationDecrement.getValue()).originParameters;
        }

        public StatusEffectInstance toStatusEffectInstance(RegistryEntry<StatusEffect> effect) {
            return StatusEffectInstanceInvoker.invokeInit(effect, this.getActuallyParameters());
        }
    }

    public static class Builder {
        private final SoakType soakType;
        private final double durationMultiplier;
        private Object2ObjectOpenHashMap<RegistryEntry<StatusEffect>, EffectEntry> effects = new Object2ObjectOpenHashMap<>();
        private boolean showInTooltip = true;

        public Builder(SoakType soakType, double durationMultiplier) {
            this.soakType = soakType;
            this.durationMultiplier = MathHelper.clamp(durationMultiplier, 0, 1);
        }

        public Builder(SoakType soakType, double durationMultiplier, SoakComponent origin) {
            this.soakType = soakType;
            this.durationMultiplier = MathHelper.clamp(durationMultiplier, 0, 1);
            this.effects = origin.effects;
        }

        public void addAll(Iterable<StatusEffectInstance> instances) {
            for (StatusEffectInstance instance : instances) {
                this.add(instance);
            }
        }

        public void add(StatusEffectInstance instance) {
            StatusEffectInstanceInvoker invoker = (StatusEffectInstanceInvoker) instance;
            this.add(instance.getEffectType(), new EffectEntry(invoker.invokeAsParameters(), new MutableDouble()));
        }

        public void add(RegistryEntry<StatusEffect> effect, EffectEntry entry) {
            entry = entry.withAmplifierMultiplierAndDurationDecrement(1, entry.originParameters.duration() - (entry.originParameters.duration() * this.durationMultiplier));
            if (this.effects.containsKey(effect)) {
                StatusEffectInstance oldEffect = this.effects.get(effect).toStatusEffectInstance(effect);
                StatusEffectInstance newEffect = entry.toStatusEffectInstance(effect);
                if (oldEffect.upgrade(newEffect)) {
                    this.effects.put(effect, entry);
                }
            } else {
                this.effects.put(effect, entry);
            }
        }

        public void invisibleInTooltip() {
            this.showInTooltip = false;
        }

        public SoakComponent build() {
            return new SoakComponent(this.soakType, this.effects, this.showInTooltip);
        }
    }

    public enum SoakType implements StringIdentifiable {
        IMMERSE(Text.translatable("soak.battlegrounds.tooltip.immerse"), 0.0057),
        LEACH(Text.translatable("soak.battlegrounds.tooltip.leach"), 1);

        public static final Codec<SoakType> CODEC = StringIdentifiable.createCodec(SoakType::values);
        public static final IntFunction<SoakType> SOAK_TYPE_INT_FUNCTION = ValueLists.createIdToValueFunction(SoakType::ordinal, SoakType.values(), ValueLists.OutOfBoundsHandling.WRAP);
        public static final PacketCodec<ByteBuf, SoakType> PACKET_CODEC = PacketCodecs.indexed(SOAK_TYPE_INT_FUNCTION, SoakType::ordinal);

        public final Text tooltipText;
        public final double durationDecrementAddPerTick;

        SoakType(Text tooltipText, double durationDecrementAddPerTick) {
            this.tooltipText = tooltipText;
            this.durationDecrementAddPerTick = durationDecrementAddPerTick;
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }
}
