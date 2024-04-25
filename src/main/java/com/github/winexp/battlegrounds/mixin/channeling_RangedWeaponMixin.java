package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.projectile.ChannelingArrowEntity;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RangedWeaponItem.class)
public abstract class channeling_RangedWeaponMixin {
    @Redirect(method = "createArrowEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArrowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;"))
    private PersistentProjectileEntity createArrow(ArrowItem instance, World world, ItemStack stack, LivingEntity shooter,
                                                   @Local(name = "weaponStack") ItemStack weaponStack) {
        if (EnchantmentHelper.getLevel(Enchantments.CHANNELING_PRO, weaponStack) > 0) {
            ChannelingArrowEntity arrow = ChannelingArrowEntity.createArrow(world, stack, shooter);
            arrow.setChanneling(true);
            return arrow;
        }
        return instance.createArrow(world, stack, shooter);
    }
}
