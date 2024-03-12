package com.github.winexp.battlegrounds.entity.projectile;

import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.network.packet.s2c.FlashS2CPacket;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.WorldUtil;
import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BiPredicate;

public class FlashBangEntity extends ThrownItemEntity {
    public static final float MAX_FLASH_TICKS = 80;
    public static final int MAX_DISTANCE = 32;
    public static final float STRENGTH_LEFT_SPEED = 0.02F;
    public static final float FOG_VISIBILITY = 3.0F;
    public static final float DISTANCE_INCREMENT = 10;
    private static final TrackedData<Integer> FUSE = DataTracker.registerData(FlashBangEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final BiPredicate<BlockRaycastResult, World> BLOCK_PREDICATE = (raycastResult, world) -> {
        BlockHitResult hitResult = raycastResult.hitResult();
        BlockPos blockPos = hitResult.getBlockPos();

        return hitResult.getType() == HitResult.Type.MISS
                || (WorldUtil.isSolidBlock(world, blockPos)
                && !WorldUtil.isTransparent(world, blockPos));
    };

    public int getFuse() {
        return this.getDataTracker().get(FUSE);
    }

    public void setFuse(int fuse) {
        this.getDataTracker().set(FUSE, fuse);
    }

    public FlashBangEntity(net.minecraft.entity.EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public FlashBangEntity(World world, double d, double e, double f) {
        super(EntityTypes.FLASH_BANG, d, e, f, world);
    }

    public FlashBangEntity(LivingEntity livingEntity, World world) {
        super(EntityTypes.FLASH_BANG, livingEntity, world);
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return (itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    public static float getFlashStrength(Entity entity, float tickDelta, Vec3d flashPos, float distance) {
        World world = entity.getWorld();
        double maxDistance = MathUtil.distanceTo(entity.getPos(), flashPos);
        EntityHitResult entityHitResult = MathUtil.raycastEntity(entity, flashPos, maxDistance, tickDelta);
        Vec3d vec3d = entity.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = MathUtil.getRotationWithEntity(entity, flashPos);
        Vec3d playerRotation = entity.getRotationVec(1.0F);
        float rotationOffset = MathUtil.getOffset(vec3d2, playerRotation);
        float rotationAttenuate = Math.max(0, Math.min(distance - 0.1F, rotationOffset - 0.85F));
        BlockRaycastResult raycastResult = MathUtil.raycastBlock(entity, flashPos, vec3d, RaycastContext.FluidHandling.NONE, BLOCK_PREDICATE);
        BlockPos blockPos = raycastResult.hitResult().getBlockPos();
        if (entityHitResult == null && (raycastResult.hitResult().getType() == HitResult.Type.MISS
                || !WorldUtil.isSolidBlock(world, blockPos)
                || WorldUtil.isTransparent(world, blockPos))) {
            return (distance - rotationAttenuate) * (FlashBangEntity.MAX_FLASH_TICKS + 20) * FlashBangEntity.STRENGTH_LEFT_SPEED * raycastResult.strength();
        } else return 0;
    }

    public static void sendFlash(World world, Vec3d pos) {
        List<? extends PlayerEntity> players = world.getPlayers();
        for (PlayerEntity player1 : players) {
            ServerPlayerEntity player = (ServerPlayerEntity) player1;
            float distance = MathUtil.distanceTo(pos, player.getEyePos());
            if (distance <= MAX_DISTANCE + DISTANCE_INCREMENT) {
                float distanceStrength = (MAX_DISTANCE - distance + DISTANCE_INCREMENT) / MAX_DISTANCE;
                FlashS2CPacket packet = new FlashS2CPacket(pos, distanceStrength);
                ServerPlayNetworking.send(player, packet);
            }
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(FUSE, 0);
    }

    @Override
    public void tick() {
        if (this.getFuse() >= 0) {
            this.setFuse(this.getFuse() - 1);
            if (this.getFuse() <= 0) {
                BlockHitResult hitResult = BlockHitResult.createMissed(this.getPos(), Direction.UP, this.getBlockPos());
                this.onCollision(hitResult);
            }
        }
        super.tick();
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; ++i) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
        else super.handleStatus(status);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("fuse")) this.setFuse(nbt.getInt("fuse"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("fuse", this.getFuse() < 0 ? -1 : this.getFuse());
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 1.0F);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte) 3);
            sendFlash(this.getWorld(), this.getPos());
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FLASH_BANG;
    }
}
