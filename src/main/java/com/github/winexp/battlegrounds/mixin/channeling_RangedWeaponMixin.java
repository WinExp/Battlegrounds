package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.projectile.ChannelingArrowEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RangedWeaponItem.class)
public abstract class channeling_RangedWeaponMixin {
    @Unique
    private ItemStack weaponStack;

    @Inject(method = "createArrowEntity", at = @At("HEAD"))
    private void getWeaponStack(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical, CallbackInfoReturnable<ProjectileEntity> cir) {
        this.weaponStack = weaponStack;
    }

    @Redirect(method = "createArrowEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArrowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;"))
    private PersistentProjectileEntity createArrow(ArrowItem instance, World world, ItemStack stack, LivingEntity shooter) {
        if (EnchantmentHelper.getLevel(Enchantments.CHANNELING_PRO, this.weaponStack) > 0) {
            ChannelingArrowEntity arrow = ChannelingArrowEntity.createArrow(world, stack, shooter);
            arrow.setChanneling(true);
            return arrow;
        }
        return instance.createArrow(world, stack, shooter);
    }
}
