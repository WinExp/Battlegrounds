package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.component.DataComponentTypes;
import com.github.winexp.battlegrounds.component.SoakComponent;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class soak_PlayerEntityMixin {
    @Shadow
    public abstract float getAttackCooldownProgress(float baseTime);

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Unique
    private float lastAttackCooldown;

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V"))
    private void getAttackCooldown(Entity target, CallbackInfo ci) {
        this.lastAttackCooldown = this.getAttackCooldownProgress(0.5F);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.AFTER))
    private void attack(Entity target, CallbackInfo ci) {
        ItemStack stack = this.getEquippedStack(EquipmentSlot.MAINHAND);
        ComponentMap components = stack.getComponents();
        if (this.lastAttackCooldown > 0.9F && target instanceof LivingEntity livingEntity && components.contains(DataComponentTypes.LEACH_DATA)) {
            SoakComponent component = components.get(DataComponentTypes.LEACH_DATA);
            assert component != null;
            component.effects().forEach((effect, entry) -> livingEntity.addStatusEffect(entry.toStatusEffectInstance(effect)));
            if (component.effects().isEmpty()) {
                stack.remove(DataComponentTypes.LEACH_DATA);
                livingEntity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        }
    }
}
