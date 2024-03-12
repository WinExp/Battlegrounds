package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.util.WorldUtil;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;

public class RupertsTearItem extends ToolItem {
    public static final Identifier IDENTIFIER = new Identifier("battlegrounds", "ruperts_tear");
    private static final int DEFAULT_COOLDOWN = 60 * 20;
    private static final int FAILED_COOLDOWN = 20;
    private static final int DEFAULT_MAX_DISTANCE = 50;

    private static final BiPredicate<BlockRaycastResult, World> BLOCK_PREDICATE = (raycastResult, world) -> {
        BlockHitResult hitResult = raycastResult.hitResult();
        BlockPos blockPos = hitResult.getBlockPos();

        return hitResult.getType() == HitResult.Type.MISS
                || (WorldUtil.isFullCube(world, blockPos));
    };

    public RupertsTearItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    private void damageStack(ItemStack stack, LivingEntity entity) {
        stack.damage(1, entity, (e) ->
                e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
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
        ItemStack itemStack = user.getStackInHand(hand);
        NbtCompound nbt = itemStack.getNbt();
        if (!world.isClient) {
            Vec3d begin = user.getEyePos();
            Vec3d rotation = user.getRotationVector();
            Vec3d end = begin.add(rotation.multiply(getMaxDistance(nbt)));
            BlockRaycastResult raycastResult = MathUtil.raycastBlock(user, begin, end, RaycastContext.FluidHandling.NONE, BLOCK_PREDICATE);

            BlockPos pos = raycastResult.hitResult().getBlockPos();
            if (!world.getWorldBorder().contains(pos)
            || raycastResult.hitResult().getType() == HitResult.Type.MISS
            || !world.isDirectionSolid(pos, user, Direction.UP)) {
                this.onUseFailed(user);
                return TypedActionResult.fail(itemStack);
            }
            for (int y = pos.getY() + 1; y <= pos.getY() + 2; y++) {
                BlockPos pos2 = new BlockPos(pos.getX(), y, pos.getZ());
                Fluid fluid = world.getBlockState(pos2).getFluidState().getFluid();
                if ((!WorldUtil.canMobSpawnInside(world, pos2)
                        && !fluid.matchesType(Fluids.WATER)
                        && !fluid.matchesType(Fluids.FLOWING_WATER))
                        || fluid.matchesType(Fluids.LAVA)
                        || fluid.matchesType(Fluids.FLOWING_LAVA)) {
                    this.onUseFailed(user);
                    return TypedActionResult.fail(itemStack);
                }
            }
            Vec3d tpPos = pos.up().toCenterPos();
            user.getItemCooldownManager().set(this, getCooldown(nbt));
            user.teleport(tpPos.x, tpPos.y, tpPos.z);
            user.onLanding();
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);
            this.damageStack(itemStack, user);
            return TypedActionResult.success(itemStack);
        }
        return TypedActionResult.pass(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        float cooldownSeconds =  (float) getCooldown(nbt) / 20;
        int maxDistance = getMaxDistance(nbt);
        tooltip.add(Text.translatable("item.battlegrounds.ruperts_tear.cooldown", cooldownSeconds)
                .formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.battlegrounds.ruperts_tear.max_distance", maxDistance)
                .formatted(Formatting.GRAY));
    }

    public static int getMaxDistance(NbtCompound nbt) {
        int maxDistance = DEFAULT_MAX_DISTANCE;
        if (nbt != null && nbt.contains("max_distance")) {
            maxDistance = nbt.getInt("max_distance");
        }
        return maxDistance;
    }

    public static int getCooldown(NbtCompound nbt) {
        int cooldown = DEFAULT_COOLDOWN;
        if (nbt != null && nbt.contains("cooldown")) {
            cooldown = nbt.getInt("cooldown");
        }
        return cooldown;
    }
}
