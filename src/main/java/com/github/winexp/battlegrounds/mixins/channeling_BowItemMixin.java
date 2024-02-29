package com.github.winexp.battlegrounds.mixins;

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
public abstract class channeling_BowItemMixin {
    @Unique
    private ItemStack tmpStack;

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    private void getStack(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        this.tmpStack = stack;
    }

    @Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArrowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;"))
    private PersistentProjectileEntity createArrow(ArrowItem instance, World world, ItemStack stack, LivingEntity shooter) {
        if (EnchantmentHelper.getLevel(Enchantments.CHANNELING_PRO, tmpStack) > 0) {
            ChannelingArrowEntity arrow = ChannelingArrowEntity.createArrow(world, stack, shooter);
            arrow.setChanneling(true);
            return arrow;
        }
        return instance.createArrow(world, stack, shooter);
    }
}
