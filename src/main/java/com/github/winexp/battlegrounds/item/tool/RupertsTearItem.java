package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.network.payload.c2s.play.RupertsTearTeleportPayloadC2S;
import com.github.winexp.battlegrounds.registry.tag.ModFluidTags;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.BlockUtil;
import com.github.winexp.battlegrounds.util.ParticleUtil;
import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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
    public static final int FAILED_COOLDOWN = 30;

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

    public static void damageStack(ServerPlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        stack.damage(1, player, slot);
    }

    public static void teleport(ServerPlayerEntity player, Vec3d teleportPos, double distance) {
        ServerWorld world = player.getServerWorld();
        Vec3d pos = player.getPos();
        int cooldown = (int) (RupertsTearItem.MAX_COOLDOWN * (Math.pow(distance, 1.5) / Math.pow(RupertsTearItem.MAX_DISTANCE, 1.5)));
        player.getItemCooldownManager().set(Items.RUPERTS_TEAR, Math.max(RupertsTearItem.MIN_COOLDOWN, cooldown));
        player.requestTeleport(teleportPos.x, teleportPos.y, teleportPos.z);
        player.onLanding();
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);

        Vec3d posOffset = teleportPos.subtract(pos);
        double ratio = Math.max(Math.max(Math.abs(posOffset.x), Math.abs(posOffset.z)), Math.abs(posOffset.y));
        Vec3d particleSpeed = posOffset.multiply(1 / ratio).multiply(0.5);
        for (int i = 1; i <= posOffset.length() / particleSpeed.length(); i++) {
            Vec3d particlePos = pos.add(particleSpeed.multiply(i));
            ParticleUtil.spawnForceLongParticle(ParticleTypes.WHITE_SMOKE, world, particlePos.x, particlePos.y, particlePos.z, 2, 0.15, 0.15, 0.15, 0.015);
        }
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
            RupertsTearTeleportPayloadC2S packet = new RupertsTearTeleportPayloadC2S(hand, tpPos);
            ClientPlayNetworking.send(packet);
        }
        return TypedActionResult.success(stack, world.isClient);
    }
}
