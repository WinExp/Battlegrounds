package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.component.DataComponentTypes;
import com.github.winexp.battlegrounds.component.SoakComponent;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class soak_ItemStackMixin {
    @Shadow
    @Final
    ComponentMapImpl components;

    @Inject(method = "inventoryTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;inventoryTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V"))
    private void inventoryTick(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (selected && entity instanceof LivingEntity livingEntity && this.components.contains(DataComponentTypes.SOAK_DATA)) {
            SoakComponent component = this.components.get(DataComponentTypes.SOAK_DATA);
            assert component != null;
            component.immerseEffects().forEach(effect -> livingEntity.addStatusEffect(new StatusEffectInstance(effect)));
        }
    }
}
