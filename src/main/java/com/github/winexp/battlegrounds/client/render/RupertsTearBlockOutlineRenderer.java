package com.github.winexp.battlegrounds.client.render;

import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.tool.RupertsTearItem;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Objects;

public class RupertsTearBlockOutlineRenderer implements WorldRenderEvents.BeforeBlockOutline {
    private static final double LERP_DELTA = 0.0165;
    private static final long LERP_DURATION = 1000 * 1000;
    private static final Vec3d NaN = new Vec3d(Double.NaN, Double.NaN, Double.NaN);

    private Vec3d prevPos = NaN;
    private long prevTime = System.nanoTime();

    private void resetData() {
        this.prevPos = NaN;
    }

    @Override
    public boolean beforeBlockOutline(WorldRenderContext context, HitResult hitResult) {
        BlockHitResult crosshairBlockTarget = (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) ? BlockHitResult.createMissed(null, null, null) : (BlockHitResult) hitResult;
        VertexConsumerProvider vertexConsumerProvider = Objects.requireNonNull(context.consumers());
        Camera camera = context.camera();
        World world = context.world();
        MatrixStack matrices = context.matrixStack();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
        Vec3d cameraPos = camera.getPos();
        Entity entity = camera.getFocusedEntity();
        float tickDelta = context.tickDelta();
        if (entity instanceof PlayerEntity player
                && world != null
                && (player.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.RUPERTS_TEAR)
                || player.getEquippedStack(EquipmentSlot.OFFHAND).isOf(Items.RUPERTS_TEAR))
                && !player.getItemCooldownManager().isCoolingDown(Items.RUPERTS_TEAR)) {
            Vec3d begin = entity.getCameraPosVec(tickDelta);
            Vec3d rotation = entity.getRotationVec(tickDelta);
            Vec3d end = begin.add(rotation.multiply(RupertsTearItem.MAX_DISTANCE));
            BlockRaycastResult raycastResult = MathUtil.raycastBlock(entity, begin, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, MathUtil.NONE_ABORT_PREDICATE, MathUtil.NONE_STRENGTH_FUNCTION);
            BlockHitResult blockHitResult = raycastResult.hitResult();
            BlockPos blockPos = blockHitResult.getBlockPos();
            long currentTime = System.nanoTime();
            float colorR, colorG, colorB, colorA;
            if (blockHitResult.getType() == HitResult.Type.MISS
                    || !world.getWorldBorder().contains(blockPos)) {
                this.resetData();
                return true;
            }
            if (RupertsTearItem.isSafe(world, blockHitResult)) {
                colorR = 0.4F;
                colorG = 0.4F;
                colorB = 1.0F;
                colorA = 0.9F;
            } else {
                colorR = 1.0F;
                colorG = 0.3F;
                colorB = 0.3F;
                colorA = 0.8F;
            }
            double lerpX = this.calculateLerp(currentTime, Double.isNaN(this.prevPos.x) ? blockPos.getX() : this.prevPos.x, blockPos.getX());
            double lerpY = this.calculateLerp(currentTime, Double.isNaN(this.prevPos.y) ? blockPos.getY() : this.prevPos.y, blockPos.getY());
            double lerpZ = this.calculateLerp(currentTime, Double.isNaN(this.prevPos.z) ? blockPos.getZ() : this.prevPos.z, blockPos.getZ());
            BlockPos roundedPos = new BlockPos(
                    (int) Math.round(lerpX),
                    (int) Math.round(lerpY),
                    (int) Math.round(lerpZ)
            );
            BlockState blockState = world.getBlockState(roundedPos);
            this.drawCuboidShapeOutline(matrices, vertexConsumer, blockState.getOutlineShape(world, roundedPos, ShapeContext.of(entity)), lerpX - cameraPos.x, lerpY - cameraPos.y, lerpZ - cameraPos.z, colorR, colorG, colorB, colorA);
            if (currentTime - this.prevTime >= LERP_DURATION) {
                this.prevPos = new Vec3d(lerpX, lerpY, lerpZ);
                this.prevTime = System.nanoTime();
            }
            return !(crosshairBlockTarget.getType() == HitResult.Type.BLOCK && blockPos.equals(crosshairBlockTarget.getBlockPos()));
        } else {
            this.resetData();
            return true;
        }
    }

    private double calculateLerp(long currentTime, double start, double end) {
        double result = start;
        double count = (double) (currentTime - this.prevTime) / LERP_DURATION;
        for (int i = 0; i < count; i++) {
            result = MathHelper.lerp(LERP_DELTA, result, end);
        }
        double odd = count - Math.floor(count);
        if (odd > 0) {
            result = MathHelper.lerp(odd * LERP_DELTA, result, end);
        }
        return result;
    }

    private void drawCuboidShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha) {
        MatrixStack.Entry entry = matrices.peek();
        shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
            float k = (float) (maxX - minX);
            float l = (float) (maxY - minY);
            float m = (float) (maxZ - minZ);
            float n = MathHelper.sqrt(k * k + l * l + m * m);
            k /= n;
            l /= n;
            m /= n;
            vertexConsumer.vertex(entry.getPositionMatrix(), (float) (minX + offsetX), (float) (minY + offsetY), (float) (minZ + offsetZ)).color(red, green, blue, alpha).normal(entry.getNormalMatrix(), k, l, m).next();
            vertexConsumer.vertex(entry.getPositionMatrix(), (float) (maxX + offsetX), (float) (maxY + offsetY), (float) (maxZ + offsetZ)).color(red, green, blue, alpha).normal(entry.getNormalMatrix(), k, l, m).next();
        });
    }
}
