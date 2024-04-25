package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.item.EnchantRestrict;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneScreenHandler.class)
public abstract class enchantRestrict_GrindstoneScreenHandlerMixin {
    @Unique
    private boolean isGrindable(ItemStack stack) {
        if (stack.getItem() instanceof EnchantRestrict restrict) {
            return restrict.isGrindable(stack);
        }
        return true;
    }

    @Inject(method = "getOutputStack", at = @At("HEAD"), cancellable = true)
    private void onGrind(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> cir) {
        if (!this.isGrindable(firstInput) || this.isGrindable(secondInput)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
