package com.github.winexp.battlegrounds.mixins;

import com.github.winexp.battlegrounds.item.Items;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Redirect(method = "isAcceptableItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private boolean isAcceptable(EnchantmentTarget instance, Item item) {
        return Items.ENCHANTMENT_PREDICATE.test(item) && instance.isAcceptableItem(item);
    }
}
