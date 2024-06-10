package com.github.winexp.battlegrounds.component;

import net.minecraft.component.DataComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class DataComponentTypes extends net.minecraft.component.DataComponentTypes {
    public static final DataComponentType<SoakComponent> IMMERSE_DATA = register("immerse_data", builder ->
            builder.codec(SoakComponent.CODEC).packetCodec(SoakComponent.PACKET_CODEC).cache());
    public static final DataComponentType<SoakComponent> LEACH_DATA = register("leach_data", builder ->
            builder.codec(SoakComponent.CODEC).packetCodec(SoakComponent.PACKET_CODEC).cache());

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, new Identifier("battlegrounds", id), builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void bootstrap() {
    }
}
