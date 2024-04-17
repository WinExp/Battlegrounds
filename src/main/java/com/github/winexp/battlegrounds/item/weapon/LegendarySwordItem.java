package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.entity.effect.StatusEffectEntry;
import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class LegendarySwordItem extends SwordItem {
    private final List<StatusEffectEntry> enrichEffects;
    private final List<StatusEffectEntry> attackEffects;
    private final int attackEffectBound;

    public LegendarySwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.enrichEffects = settings.enrichEffects;
        this.attackEffects = settings.attackEffects;
        this.attackEffectBound = settings.attackEffectBound;
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.getEquippedStack(EquipmentSlot.MAINHAND).isOf(this)
            || player.getEquippedStack(EquipmentSlot.OFFHAND).isOf(this)) {
                for (StatusEffectEntry effectEntry : this.enrichEffects) {
                    player.addStatusEffect(new StatusEffectInstance(effectEntry.statusEffect(), effectEntry.duration(), effectEntry.amplifier()), player);
                }
            }
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.battlegrounds.legendary")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHit(stack, target, attacker);
        Random random = attacker.getRandom();
        if (random.nextBetween(1, 100) <= this.attackEffectBound) {
            for (StatusEffectEntry effectEntry : attackEffects) {
                target.addStatusEffect(new StatusEffectInstance(effectEntry.statusEffect(), effectEntry.duration(), effectEntry.amplifier()), attacker);
            }
        }
        return true;
    }

    public static class Settings extends Item.Settings {
        private final List<StatusEffectEntry> enrichEffects;
        private final List<StatusEffectEntry> attackEffects;
        private int attackEffectBound;

        public Settings() {
            super();
            this.enrichEffects = new ArrayList<>(List.of(
                    new StatusEffectEntry(StatusEffects.RESISTANCE, 0, 2 * 20)
            ));
            this.attackEffects = new ArrayList<>(List.of(
                    new StatusEffectEntry(StatusEffects.APPROACHING_EXTINCTION, 0, 15 * 20)
            ));
            this.attackEffectBound = 100;
        }

        public Settings ignoreEnrichEffects() {
            this.enrichEffects.clear();
            return this;
        }

        public Settings enrichEffect(StatusEffectEntry effectEntry) {
            this.enrichEffects.add(effectEntry);
            return this;
        }

        public Settings ignoreAttackEffects() {
            this.attackEffects.clear();
            return this;
        }

        public Settings attackEffect(StatusEffectEntry effectEntry) {
            this.attackEffects.add(effectEntry);
            return this;
        }

        public Settings attackEffectBound(int bound) {
            this.attackEffectBound = bound;
            return this;
        }
    }
}
