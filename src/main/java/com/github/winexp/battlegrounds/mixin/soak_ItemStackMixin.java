package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.component.DataComponentTypes;
import com.github.winexp.battlegrounds.component.SoakComponent;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.DataComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipAppender;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class soak_ItemStackMixin {
    @Shadow
    @Final
    ComponentMapImpl components;

    @Shadow
    protected abstract <T extends TooltipAppender> void appendTooltip(DataComponentType<T> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type);

    @Shadow
    @Nullable
    public abstract <T> T remove(DataComponentType<? extends T> type);

    @Inject(method = "inventoryTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;inventoryTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V"))
    private void inventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world.getTickManager().shouldTick() && selected && entity instanceof LivingEntity livingEntity && this.components.contains(DataComponentTypes.IMMERSE_DATA)) {
            SoakComponent component = this.components.get(DataComponentTypes.IMMERSE_DATA);
            assert component != null;
            for (var iterator = component.effects().object2ObjectEntrySet().fastIterator(); iterator.hasNext();) {
                var mapEntry = iterator.next();
                RegistryEntry<StatusEffect> effect = mapEntry.getKey();
                SoakComponent.EffectEntry entry = mapEntry.getValue();
                livingEntity.addStatusEffect(entry.toStatusEffectInstance(effect));
                if (entry.originParameters().duration() != StatusEffectInstance.INFINITE) {
                    entry.durationDecrement().add(0.0057);
                }
                if (!entry.isValid()) {
                    iterator.remove();
                }
            }
            if (component.effects().isEmpty()) {
                this.remove(DataComponentTypes.IMMERSE_DATA);
                livingEntity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/DataComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/client/item/TooltipType;)V", ordinal = 2, shift = At.Shift.AFTER))
    private void appendTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir, @Local Consumer<Text> appender) {
        this.appendTooltip(DataComponentTypes.IMMERSE_DATA, context, appender, type);
        this.appendTooltip(DataComponentTypes.LEACH_DATA, context, appender, type);
    }
}
