package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.item.EnchantRestrict;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class enchantRestrict_EnchantmentMixin {
    @Shadow
    @Final
    private Enchantment.Properties properties;

    @Inject(method = "isAcceptableItem", at = @At("RETURN"), cancellable = true)
    private void isAcceptable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Item item = stack.getItem();
        if (item instanceof EnchantRestrict restrict) {
            cir.setReturnValue(restrict.isEnchantable((Enchantment) (Object) this, this.properties));
        }
    }
}
