package com.github.winexp.battlegrounds.registry.tag;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModFluidTags {
    public static final TagKey<Fluid> RUPERTS_TEAR_IGNORED = of("ruperts_tear_ignored");

    private static TagKey<Fluid> of(String id) {
        return TagKey.of(RegistryKeys.FLUID, new Identifier("battlegrounds", id));
    }
}
