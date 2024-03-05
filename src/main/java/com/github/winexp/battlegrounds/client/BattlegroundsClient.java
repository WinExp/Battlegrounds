package com.github.winexp.battlegrounds.client;

import com.github.winexp.battlegrounds.client.gui.screen.VoteScreen;
import com.github.winexp.battlegrounds.client.render.ChannelingArrowEntityRenderer;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.util.Constants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class BattlegroundsClient implements ClientModInitializer {
    private KeyBinding voteKeyBinding;

    private void registerRenderer() {
        EntityRendererRegistry.register(EntityTypes.CHANNELING_ARROW, ChannelingArrowEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.FLASH_BANG, FlyingItemEntityRenderer::new);
    }

    private void registerKeyBinding() {
        voteKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.battlegrounds.vote_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key.categories.battlegrounds"
        ));
    }

    private void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(Constants.FLASH_BANG_PACKET_ID, (client, handler, buf, responseSender) -> {
            float distance = buf.readFloat();
            Vec3d flashPos = buf.readVec3d();
            this.tryFlash(client, flashPos, distance);
        });
    }

    private void tryFlash(MinecraftClient client, Vec3d flashPos, float distance) {
        client.execute(() -> {
            Entity entity = client.getCameraEntity();
            if (entity != null) {
                float tickDelta = client.getTickDelta();
                ClientVariables.flashStrength = Math.max(ClientVariables.flashStrength,
                        FlashBangEntity.getFlashStrength(entity, tickDelta, flashPos, distance));
            }
        });
    }

    private void tick(MinecraftClient client) {
        // 闪光弹
        if (ClientVariables.flashStrength > 0) {
            ClientVariables.flashStrength -= FlashBangEntity.STRENGTH_LEFT_SPEED;
        }
        else if (ClientVariables.flashStrength < 0){
            ClientVariables.flashStrength = 0;
        }

        // 按键绑定
        while (voteKeyBinding.wasPressed()) {
            client.setScreen(new VoteScreen());
        }
    }

    @Override
    public void onInitializeClient() {
        // 注册实体渲染器
        this.registerRenderer();
        // 注册网络包接收器
        this.registerReceiver();
        // 注册按键绑定
        this.registerKeyBinding();
        // 注册 tick
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }
}
