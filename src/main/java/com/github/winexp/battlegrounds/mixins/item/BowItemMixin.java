package com.github.winexp.battlegrounds.mixins.item;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.projectile.ChannelingArrowEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public class BowItemMixin {
    @Unique
    private ItemStack tempStack;

    @Inject(method = "onStoppedUsing", at = @At(value = "HEAD"))
    private void onStopUse1(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci){
        this.tempStack = stack;
    }

    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"))
    private void onStopUse(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci){
        //this.tempStack = stack;
    }

    @Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArrowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;"))
    private PersistentProjectileEntity createArrow(ArrowItem instance, World world, ItemStack stack, LivingEntity shooter){
        if (EnchantmentHelper.getLevel(Enchantments.CHANNELING_PRO, tempStack) > 0){
            ChannelingArrowEntity arrow = ChannelingArrowEntity.createArrow(world, stack, shooter);
            arrow.setChanneling(true);
            return arrow;
        }
        return instance.createArrow(world, stack, shooter);
    }
}
