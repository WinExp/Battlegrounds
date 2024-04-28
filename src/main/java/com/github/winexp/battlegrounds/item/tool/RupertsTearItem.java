package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.network.payload.c2s.play.RupertsTearTeleportPayloadC2S;
import com.github.winexp.battlegrounds.registry.tag.ModFluidTags;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.BlockUtil;
import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class RupertsTearItem extends ToolItem implements EnchantRestrict {
    public static final int MAX_DISTANCE = 100;
    public static final int MAX_COOLDOWN = 30 * 20;
    public static final int MIN_COOLDOWN = 3 * 20;
    public static final int FAILED_COOLDOWN = 20;

    public RupertsTearItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    private void onUseFailed(PlayerEntity player) {
        player.getItemCooldownManager().set(this, FAILED_COOLDOWN);
        player.sendMessage(Text.translatable("item.battlegrounds.ruperts_tear.use_failed"), true);
    }

    public static boolean isSafe(World world, BlockHitResult blockHitResult) {
        BlockPos pos = blockHitResult.getBlockPos();
        if (blockHitResult.getType() == HitResult.Type.MISS
                || !world.getWorldBorder().contains(pos)) {
            return false;
        }
        for (int y = pos.getY() + 1; y <= pos.getY() + 2; y++) {
            BlockPos pos2 = new BlockPos(pos.getX(), y, pos.getZ());
            BlockState blockState = world.getBlockState(pos2);
            FluidState fluidState = blockState.getFluidState();

            if (!BlockUtil.canMobSpawnInside(world, pos2)
                    || (!fluidState.isEmpty() && !fluidState.isIn(ModFluidTags.RUPERTS_TEAR_IGNORED))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) {
            Vec3d begin = user.getEyePos();
            Vec3d rotation = user.getRotationVector();
            Vec3d end = begin.add(rotation.multiply(MAX_DISTANCE));
            BlockRaycastResult raycastResult = MathUtil.raycastBlock(user, begin, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, MathUtil.NONE_ABORT_PREDICATE, MathUtil.NONE_STRENGTH_FUNCTION);

            BlockPos pos = raycastResult.hitResult().getBlockPos();
            if (!isSafe(world,raycastResult.hitResult())) {
                this.onUseFailed(user);
                return TypedActionResult.success(stack);
            }
            double y = BlockUtil.getBlockMaxY(world, pos);
            Box boundingBox = BlockUtil.getCollisionShape(world, pos).getBoundingBox();
            Vec3d tpPos = boundingBox.getCenter().add(Vec3d.of(pos)).withAxis(Direction.Axis.Y, y);
            RupertsTearTeleportPayloadC2S packet = new RupertsTearTeleportPayloadC2S(stack, tpPos);
            ClientPlayNetworking.send(packet);
        }
        return TypedActionResult.success(stack, world.isClient);
    }
}
