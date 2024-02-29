package com.github.winexp.battlegrounds.mixins;

import com.github.winexp.battlegrounds.item.EnchantRestrict;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(GrindstoneScreenHandler.class)
public abstract class deny_GrindstoneScreenHandlerMixin {
    @Invoker("grind")
    public abstract ItemStack invokeGrind(ItemStack item, int damage, int amount);

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/GrindstoneScreenHandler;grind(Lnet/minecraft/item/ItemStack;II)Lnet/minecraft/item/ItemStack;"))
    private ItemStack getGrindResult(GrindstoneScreenHandler instance, ItemStack item, int damage, int amount) {
        boolean bl = true;
        if (item.getItem() instanceof EnchantRestrict restrict) {
            bl = restrict.isGrindable();
        }
        if (bl) return this.invokeGrind(item, damage, amount);
        else return ItemStack.EMPTY;
    }
}
