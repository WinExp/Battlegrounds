package com.github.winexp.battlegrounds.network.task.s2c.config;

import com.github.winexp.battlegrounds.network.packet.s2c.config.ModVersionS2CPacket;
import com.github.winexp.battlegrounds.util.Constants;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerConfigurationTask;

import java.util.function.Consumer;

public record ModVersionConfigurationTask(ServerConfigurationNetworkHandler handler) implements ServerPlayerConfigurationTask {
    public static final Key KEY = new Key(ModVersionS2CPacket.ID.toString());

    @Override
    public void sendPacket(Consumer<Packet<?>> sender) {
        ServerConfigurationNetworking.send(this.handler, new ModVersionS2CPacket(Constants.MOD_VERSION));
    }

    @Override
    public Key getKey() {
        return KEY;
    }
}
