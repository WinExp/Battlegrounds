package com.github.winexp.battlegrounds.client;

import com.github.winexp.battlegrounds.client.gui.screen.vote.VoteScreen;
import com.github.winexp.battlegrounds.client.network.ModClientConfigurationNetworkHandler;
import com.github.winexp.battlegrounds.client.network.ModClientPlayNetworkHandler;
import com.github.winexp.battlegrounds.client.render.FlashRenderer;
import com.github.winexp.battlegrounds.client.render.entity.ChannelingArrowEntityRenderer;
import com.github.winexp.battlegrounds.client.toast.vote.VoteClosedToast;
import com.github.winexp.battlegrounds.client.toast.vote.VoteOpenedToast;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.event.ClientApplyFogCallback;
import com.github.winexp.battlegrounds.event.ClientVoteEvents;
import com.github.winexp.battlegrounds.item.Items;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

@Environment(EnvType.CLIENT)
public class BattlegroundsClient implements ClientModInitializer {
    private void registerRenderer() {
        // 实体渲染器
        EntityRendererRegistry.register(EntityTypes.CHANNELING_ARROW, ChannelingArrowEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.FLASH_BANG, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.MOLOTOV, FlyingItemEntityRenderer::new);

        // 自定义渲染器
        FlashRenderer flashRenderer = new FlashRenderer();
        HudRenderCallback.EVENT.register(flashRenderer);
        ClientApplyFogCallback.EVENT.register(flashRenderer);
    }

    @Override
    public void onInitializeClient() {
        // 注册事件
        ClientVoteEvents.OPENED.register(voteInfo -> {
            MinecraftClient client = MinecraftClient.getInstance();
            client.getToastManager().add(new VoteOpenedToast(voteInfo));
            VoteScreen.onVoteOpened(client, voteInfo);
        });
        ClientVoteEvents.CLOSED.register(((voteInfo, reason) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            client.getToastManager().add(new VoteClosedToast(voteInfo, reason));
            VoteScreen.onVoteClosed(client, voteInfo);
        }));
        ClientTickEvents.END_CLIENT_TICK.register(VoteScreen::globalTick);
        // 注册实体渲染器
        this.registerRenderer();
        // 注册网络包相关
        ModClientConfigurationNetworkHandler.register();
        ModClientPlayNetworkHandler.registerReceivers();
        // 注册按键绑定
        KeyBindings.registerKeyBindings();
        // 注册物品模型谓词
        Items.registerModelPredicates();
    }
}
