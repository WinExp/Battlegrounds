package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.util.RecipeUtil;
import com.google.gson.JsonElement;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class customRecipe_RecipeManagerMixin {
    @Shadow
    public abstract Collection<RecipeEntry<?>> values();

    @Shadow
    public abstract void setRecipes(Iterable<RecipeEntry<?>> recipes);

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("TAIL"))
    private void applyCustomRecipes(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        List<RecipeEntry<?>> list = new ArrayList<>(this.values());
        list.addAll(RecipeUtil.getRecipes());
        this.setRecipes(list);
    }
}
