package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.registry.tag.ModFluidTags;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.WorldUtil;
import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class RupertsTearItem extends ToolItem implements EnchantRestrict {
    private static final int MAX_COOLDOWN = 30 * 20;
    private static final int MIN_COOLDOWN = 4 * 20;
    private static final int FAILED_COOLDOWN = 20;
    private static final int MAX_DISTANCE = 70;

    public RupertsTearItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    private void damageStack(ItemStack stack, LivingEntity entity) {
        stack.damage(1, entity, (e) ->
                e.sendToolBreakStatus(Hand.MAIN_HAND));
    }

    private void onUseFailed(PlayerEntity player) {
        player.getItemCooldownManager().set(this, FAILED_COOLDOWN);
        player.sendMessage(Text.translatable("item.battlegrounds.ruperts_tear.use_failed"), true);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            Vec3d begin = user.getEyePos();
            Vec3d rotation = user.getRotationVector();
            Vec3d end = begin.add(rotation.multiply(MAX_DISTANCE));
            BlockRaycastResult raycastResult = MathUtil.raycastBlock(user, begin, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, MathUtil.NONE_ABORT_PREDICATE, MathUtil.NONE_STRENGTH_FUNCTION);

            BlockPos pos = raycastResult.hitResult().getBlockPos();
            if (!world.getWorldBorder().contains(pos)
            || raycastResult.hitResult().getType() == HitResult.Type.MISS) {
                this.onUseFailed(user);
                return TypedActionResult.fail(stack);
            }
            for (int y = pos.getY() + 1; y <= pos.getY() + 2; y++) {
                BlockPos pos2 = new BlockPos(pos.getX(), y, pos.getZ());
                BlockState blockState = world.getBlockState(pos2);
                FluidState fluidState = blockState.getFluidState();

                if (!WorldUtil.canMobSpawnInside(world, pos2)
                        || (!fluidState.isEmpty() && !fluidState.isIn(ModFluidTags.RUPERTS_TEAR_IGNORED))) {
                    this.onUseFailed(user);
                    return TypedActionResult.fail(stack);
                }
            }
            double y = WorldUtil.getBlockTopY(world, pos);
            Box boundingBox = WorldUtil.getCollisionShape(world, pos).getBoundingBox();
            Vec3d tpPos = boundingBox.getCenter().add(Vec3d.of(pos)).withAxis(Direction.Axis.Y, y);
            double distance = tpPos.distanceTo(user.getPos());
            int cooldown = (int) (MAX_COOLDOWN * (Math.pow(distance, 1.5) / Math.pow(MAX_DISTANCE, 1.5)));
            user.getItemCooldownManager().set(this, Math.max(MIN_COOLDOWN, cooldown));
            user.teleport(tpPos.x, tpPos.y, tpPos.z);
            user.onLanding();
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);
            this.damageStack(stack, user);
        }
        return TypedActionResult.success(stack, world.isClient);
    }
}
