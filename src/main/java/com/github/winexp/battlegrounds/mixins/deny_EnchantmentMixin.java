package com.github.winexp.battlegrounds.mixins;

import com.github.winexp.battlegrounds.item.EnchantRestrict;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Enchantment.class)
public abstract class deny_EnchantmentMixin {
    @Redirect(method = "isAcceptableItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private boolean isAcceptable(EnchantmentTarget instance, Item item) {
        boolean bl = true;
        if (item instanceof EnchantRestrict restrict) {
            bl = restrict.isEnchantable((Enchantment) (Object) this, instance);
        }
        return bl && instance.isAcceptableItem(item);
    }
}
