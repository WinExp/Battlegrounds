package com.github.winexp.battlegrounds.item.weapon;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class LegendarySwordItem extends SwordItem {
    private final static Map<StatusEffect, Integer> legendaryEnrichStatusEffects = Map.of(
            StatusEffects.RESISTANCE, 0
    );

    public LegendarySwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.getEquippedStack(EquipmentSlot.MAINHAND).isOf(this)
            || player.getEquippedStack(EquipmentSlot.OFFHAND).isOf(this)) {
                int duration = this.getEnrichEffectsDuration();
                legendaryEnrichStatusEffects.forEach((effect, amplifier) ->
                        player.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier), player));
                this.getEnrichEffects().forEach((effect, amplifier) ->
                        player.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier), player));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.battlegrounds.legendary")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
        Random random = attacker.getRandom();
        if (random.nextBetween(1, 100) <= this.getAttackEffectsBound()) {
            int duration = this.getAttackEffectsDuration();
            this.getAttackEffects().forEach((effect, amplifier) ->
                    target.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier), attacker));
        }
        return true;
    }

    public int getEnrichEffectsDuration() {
        return 2 * 20;
    }

    @NotNull
    public abstract Map<StatusEffect, Integer> getEnrichEffects();

    public int getAttackEffectsDuration() {
        return 5 * 20;
    }

    public int getAttackEffectsBound() {
        return 100;
    }

    @NotNull
    public abstract Map<StatusEffect, Integer> getAttackEffects();

    @NotNull
    public abstract Multimap<EntityAttribute, EntityAttributeModifier> getCustomModifiers(EquipmentSlot slot);

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(slot));
        builder.putAll(this.getCustomModifiers(slot));
        return builder.build();
    }
}
