package com.github.winexp.battlegrounds.mixin;

import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(RaycastContext.ShapeType.class)
public abstract class shapeType_RaycastContextMixin {
    @Shadow
    public static RaycastContext.ShapeType[] values() {
        throw new AssertionError();
    }

    @Unique
    private static final List<RaycastContext.ShapeType> expansionVariants = new ArrayList<>();

    @Inject(method = "values", at = @At("RETURN"), cancellable = true)
    private static void onGetValues(CallbackInfoReturnable<RaycastContext.ShapeType[]> cir) {
        ArrayList<RaycastContext.ShapeType> variants = new ArrayList<>(List.of(cir.getReturnValue()));
        variants.addAll(expansionVariants);
        cir.setReturnValue(variants.toArray(new RaycastContext.ShapeType[0]));
    }

    @Unique
    private static final RaycastContext.ShapeType CULLING = addVariant("CULLING",
            (state, world, pos, context) -> state.getCullingShape(world, pos));

    @Contract(pure = true)
    @Invoker("<init>")
    public static RaycastContext.ShapeType invokeInit(String internalName, int internalId, RaycastContext.ShapeProvider provider) {
        throw new AssertionError();
    }

    @Contract(value = "_, _ -> new", pure = true)
    @Unique
    private static RaycastContext.ShapeType addVariant(String internalName, RaycastContext.ShapeProvider provider) {
        ArrayList<RaycastContext.ShapeType> variants = new ArrayList<>(List.of(RaycastContext.ShapeType.values()));
        RaycastContext.ShapeType variant = invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, provider);
        expansionVariants.add(variant);
        return variant;
    }
}
