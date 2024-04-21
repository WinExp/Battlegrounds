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

public class RupertsTearBlockOutlineRenderer implements WorldRenderEvents.DebugRender {
    private static final double LERP_DELTA = 0.1;
    private static final int LERP_DURATION = 5;
    private static final Vec3d NaN = new Vec3d(Double.NaN, Double.NaN, Double.NaN);

    private Vec3d prevPos = NaN;
    private long prevTime = System.currentTimeMillis();

    private void resetPrevPos() {
        this.prevPos = NaN;
    }

    @Override
    public void beforeDebugRender(WorldRenderContext context) {
        VertexConsumerProvider vertexConsumerProvider = Objects.requireNonNull(context.consumers());
        Camera camera = context.camera();
        World world = context.world();
        MatrixStack matrices = context.matrixStack();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
        Vec3d cameraPos = camera.getPos();
        Entity entity = camera.getFocusedEntity();
        if (entity instanceof PlayerEntity player
                && world != null
                && (player.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.RUPERTS_TEAR)
                || player.getEquippedStack(EquipmentSlot.OFFHAND).isOf(Items.RUPERTS_TEAR))
                && !player.getItemCooldownManager().isCoolingDown(Items.RUPERTS_TEAR)) {
            Vec3d begin = entity.getEyePos();
            Vec3d rotation = entity.getRotationVector();
            Vec3d end = begin.add(rotation.multiply(RupertsTearItem.MAX_DISTANCE));
            BlockRaycastResult raycastResult = MathUtil.raycastBlock(entity, begin, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, MathUtil.NONE_ABORT_PREDICATE, MathUtil.NONE_STRENGTH_FUNCTION);
            BlockHitResult blockHitResult = raycastResult.hitResult();
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            long currentTime = System.currentTimeMillis();
            double delta = Math.pow(LERP_DELTA, Math.max((currentTime - this.prevTime) / LERP_DURATION, 1));
            float colorR, colorG, colorB, colorA;
            double lerpX = MathHelper.lerp(delta, Double.isNaN(this.prevPos.x) ? blockPos.getX() : this.prevPos.x, blockPos.getX());
            double lerpY = MathHelper.lerp(delta, Double.isNaN(this.prevPos.y) ? blockPos.getY() : this.prevPos.y, blockPos.getY());
            double lerpZ = MathHelper.lerp(delta, Double.isNaN(this.prevPos.z) ? blockPos.getZ() : this.prevPos.z, blockPos.getZ());
            if (blockHitResult.getType() == HitResult.Type.MISS
                    || !world.getWorldBorder().contains(blockPos)) {
                this.resetPrevPos();
                return;
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
            drawCuboidShapeOutline(matrices, vertexConsumer, blockState.getOutlineShape(world, blockPos, ShapeContext.of(entity)), lerpX - cameraPos.x, lerpY - cameraPos.y, lerpZ - cameraPos.z, colorR, colorG, colorB, colorA);
            if (currentTime - this.prevTime >= LERP_DURATION) {
                this.prevPos = new Vec3d(lerpX, lerpY, lerpZ);
                this.prevTime = System.currentTimeMillis();
            }
        } else {
            this.resetPrevPos();
        }
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
