package com.github.winexp.battlegrounds.client;

import com.github.winexp.battlegrounds.client.render.ChannelingArrowEntityRenderer;
import com.github.winexp.battlegrounds.client.util.ClientConstants;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.util.BlockUtil;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.result.BlockRaycastResult;
import net.fabricmc.api.ClientModInitializer;
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

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class BattlegroundsClient implements ClientModInitializer {
    private void registerRenderer() {
        EntityRendererRegistry.register(EntityTypes.CHANNELING_ARROW, ChannelingArrowEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.FLASH_BANG, FlyingItemEntityRenderer::new);
    }

    private void tryFlash(MinecraftClient client, Vec3d flashPos, AtomicReference<Float> strength) {
        client.execute(() -> {
            Entity entity = client.getCameraEntity();
            if (entity != null) {
                World world = entity.getWorld();
                float tickDelta = client.getTickDelta();
                double maxDistance = MathUtil.distanceTo(entity.getPos(), flashPos);
                EntityHitResult entityHitResult = MathUtil.raycastEntity(entity, maxDistance, tickDelta, flashPos);
                Vec3d vec3d = entity.getCameraPosVec(tickDelta);
                Vec3d vec3d2 = MathUtil.getRotationWithEntity(entity, flashPos);
                Vec3d playerRotation = entity.getRotationVec(1.0F);
                float offset = MathUtil.getOffset(vec3d2, playerRotation);
                if (offset >= 0.85F) {
                    strength.set(strength.get() - offset + 0.85F);
                }
                Predicate<BlockHitResult> blockPredicate = (hitResult) -> {
                    BlockPos blockPos = hitResult.getBlockPos();
                    return hitResult.getType() == HitResult.Type.MISS
                            || (BlockUtil.isSolidBlock(world, blockPos)
                            && !BlockUtil.isTransparent(world, blockPos));
                };
                BlockRaycastResult raycastResult = MathUtil.raycastBlock(entity, vec3d, flashPos, RaycastContext.FluidHandling.NONE, blockPredicate);
                BlockPos blockPos = raycastResult.hitResult().getBlockPos();
                if (entityHitResult == null && (raycastResult.hitResult().getType() == HitResult.Type.MISS
                        || !BlockUtil.isSolidBlock(world, blockPos)
                        || BlockUtil.isTransparent(world, blockPos))) {
                    ClientVariables.INSTANCE.flashStrength = Math.max(ClientVariables.INSTANCE.flashStrength,
                            strength.get() * (FlashBangEntity.MAX_FLASH_TICKS + 20) * ClientConstants.FLASH_LEFT_SPEED * raycastResult.strength());
                }
            }
        });
    }

    private void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(Constants.FLASH_BANG_PACKET_ID, (client, handler, buf, responseSender) -> {
            float strength = buf.readFloat();
            Vec3d flashPos = buf.readVec3d();
            this.tryFlash(client, flashPos, new AtomicReference<>(strength));
        });
    }

    @Override
    public void onInitializeClient() {
        // 注册实体渲染器
        this.registerRenderer();
        // 注册网络包接收器
        this.registerReceiver();
    }
}
