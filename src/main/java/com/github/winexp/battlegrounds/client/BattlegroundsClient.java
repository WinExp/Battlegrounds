package com.github.winexp.battlegrounds.client;

import com.github.winexp.battlegrounds.client.render.ChannelingArrowEntityRenderer;
import com.github.winexp.battlegrounds.client.util.ClientConstants;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.util.BlockUtil;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.MathUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class BattlegroundsClient implements ClientModInitializer {
    private void registerRenderer() {
        EntityRendererRegistry.register(EntityTypes.CHANNELING_ARROW, ChannelingArrowEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.FLASH_BANG, FlyingItemEntityRenderer::new);
    }

    private void tryFlash(MinecraftClient client, Vec3d flashPos, float strength) {
        client.execute(() -> {
            Entity entity = client.getCameraEntity();
            if (entity != null) {
                World world = entity.getWorld();
                float tickDelta = client.getTickDelta();
                double maxDistance = MathUtil.distanceTo(entity.getPos(), flashPos);
                EntityHitResult entityHitResult = MathUtil.raycastEntity(entity, maxDistance, tickDelta, flashPos);
                Vec3d vec3d = entity.getCameraPosVec(tickDelta);
                Vec3d vec3d2 = MathUtil.getRotationWithEntity(entity, flashPos);
                Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
                BlockHitResult blockHitResult = world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity));
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (entityHitResult == null && (blockHitResult.getType() == HitResult.Type.MISS
                        || (!BlockUtil.isSolidBlock(world, blockPos) || BlockUtil.isTransparent(world, blockPos)))) {
                    ClientVariables.INSTANCE.flashStrength = strength * (FlashBangEntity.MAX_FLASH_TICKS + 20) * ClientConstants.FLASH_LEFT_SPEED;
                }
            }
        });
    }

    private void registerReceiver() {
        ClientPlayNetworking.registerReceiver(Constants.FLASH_BANG_PACKET_ID, (client, handler, buf, responseSender) -> {
            float strength = buf.readFloat();
            Vec3d flashPos = buf.readVec3d();
            this.tryFlash(client, flashPos, strength);
        });
    }

    @Override
    public void onInitializeClient() {
        // 注册实体渲染器
        this.registerRenderer();
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            // 注册网络包接收器
            this.registerReceiver();
        });
    }
}
