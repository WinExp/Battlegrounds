package com.github.winexp.battlegrounds.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

public class ScreenHandlerType<T extends ScreenHandler> extends net.minecraft.screen.ScreenHandlerType<T> {
    public static final net.minecraft.screen.ScreenHandlerType<SoakTableScreenHandler> SOAK = register("soak", SoakTableScreenHandler::new);

    private ScreenHandlerType(Factory<T> factory, FeatureSet requiredFeatures) {
        super(factory, requiredFeatures);
    }

    private static <T extends ScreenHandler> net.minecraft.screen.ScreenHandlerType<T> register(String id, Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, new Identifier("battlegrounds", id), new net.minecraft.screen.ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static void bootstrap() {
    }
}
