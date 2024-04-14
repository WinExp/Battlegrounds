package com.github.winexp.battlegrounds.entity.projectile;

import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.entity.data.ModTrackedDataHandlers;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.network.packet.s2c.play.FlashS2CPacket;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import com.github.winexp.battlegrounds.util.WorldUtil;
import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import com.mojang.serialization.Codec;
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
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FlashBangEntity extends ThrownItemEntity {
    public static final int MAX_DISTANCE = 48;
    public static final int DISTANCE_INCREMENT = 16;
    public static final int DEFAULT_DETONATION_FUSE = 10;
    private static final TrackedData<Integer> FUSE = DataTracker.registerData(FlashBangEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<FuseMode> FUSE_MODE = DataTracker.registerData(FlashBangEntity.class, ModTrackedDataHandlers.FLASH_BANG_FUSE_MODE);

    public FlashBangEntity(net.minecraft.entity.EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public FlashBangEntity(World world, double d, double e, double f) {
        super(EntityTypes.FLASH_BANG, d, e, f, world);
    }

    public FlashBangEntity(LivingEntity livingEntity, World world) {
        super(EntityTypes.FLASH_BANG, livingEntity, world);
    }

    public int getFuse() {
        return this.getDataTracker().get(FUSE);
    }

    public void setFuse(int fuse) {
        this.getDataTracker().set(FUSE, fuse);
    }

    public FuseMode getFuseMode() {
        return this.getDataTracker().get(FUSE_MODE);
    }

    public void setFuseMode(FuseMode fuseMode) {
        this.getDataTracker().set(FUSE_MODE, fuseMode);
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return (itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    public static void summonFlash(World world, Vec3d pos) {
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

    public void flashAndDiscard() {
        World world = this.getWorld();
        if (!world.isClient) {
            world.sendEntityStatus(this, (byte) 3);
            summonFlash(world, this.getPos());
            this.playSound(SoundEvents.ENTITY_FLASH_BANG_EXPLODE, 2, 1.0F);
            this.discard();
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(FUSE, 0);
        this.getDataTracker().startTracking(FUSE_MODE, FuseMode.NORMAL_FUSE);
    }

    @Override
    public void tick() {
        if (this.getFuse() >= 0) {
            this.setFuse(this.getFuse() - 1);
            if (this.getFuse() <= 0) {
                this.flashAndDiscard();
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
        if (nbt.contains("fuse_status")) this.setFuseMode(FuseMode.CODEC.parse(NbtOps.INSTANCE, nbt.get("fuse_status")).result().orElse(FuseMode.NORMAL_FUSE));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("fuse", this.getFuse() < 0 ? -1 : this.getFuse());
        nbt.put("fuse_status", Util.getResult(FuseMode.CODEC.encodeStart(NbtOps.INSTANCE, this.getFuseMode()), IllegalStateException::new));
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        this.reboundEntity();
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 2.0F);
    }

    private void playReboundSound() {
        Random random = this.getWorld().getRandom();
        this.playSound(SoundEvents.ENTITY_FLASH_BANG_REBOUND, 1.5F, RandomUtil.nextFloatBetween(random, 0.95F, 1.05F));
    }

    private void reboundEntity() {
        Vec3d velocity = this.getVelocity();
        this.setVelocity(velocity.negate().multiply(0.15));
        this.playReboundSound();
    }

    private Vec3d computeBlockRebound(Vec3d velocity, boolean isInside, Direction side) {
        if (isInside) {
            velocity = velocity.multiply(1.05);
        } else {
            double deltaX = side.getAxis() == Direction.Axis.X ? -1 : 1;
            double deltaY = side.getAxis() == Direction.Axis.Y ? -1 : 1;
            double deltaZ = side.getAxis() == Direction.Axis.Z ? -1 : 1;
            velocity = new Vec3d(velocity.x * deltaX, velocity.y * deltaY, velocity.z * deltaZ)
                    .multiply(0.4);
        }
        return velocity;
    }

    private void reboundBlock(BlockHitResult hitResult) {
        Queue<BlockHitResult> reboundQueue = new LinkedList<>();
        reboundQueue.add(hitResult);
        Vec3d velocity;
        while (!reboundQueue.isEmpty()) {
            BlockHitResult blockHitResult = reboundQueue.poll();
            velocity = this.computeBlockRebound(this.getVelocity(), blockHitResult.isInsideBlock(), blockHitResult.getSide());
            if (!blockHitResult.isInsideBlock()) {
                Vec3d targetPos = this.getPos().add(velocity);
                BlockRaycastResult raycastResult = MathUtil.raycastBlock(this, this.getPos(), targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,
                        MathUtil.NONE_ABORT_PREDICATE, MathUtil.NONE_STRENGTH_FUNCTION);
                BlockHitResult raycastHitResult = raycastResult.hitResult();
                if (raycastHitResult.getType() != HitResult.Type.MISS) {
                    this.refreshPositionAfterTeleport(raycastHitResult.getPos());
                    reboundQueue.add(raycastHitResult);
                }
            }
            double speed = velocity.length();
            if (speed <= 0.1F && blockHitResult.getSide() == Direction.UP) {
                this.setFuseMode(FuseMode.DETONATION_FUSE);
                this.setFuse(DEFAULT_DETONATION_FUSE);
                double topY = WorldUtil.getBlockTopY(this.getWorld(), this.getBlockPos().down());
                this.refreshPositionAfterTeleport(this.getX(), topY, this.getZ());
                velocity = Vec3d.ZERO;
                this.setNoGravity(true);
                this.noClip = true;
                reboundQueue.clear();
            }
            this.setVelocity(velocity);
            this.playReboundSound();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.reboundBlock(blockHitResult);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FLASH_BANG;
    }

    public enum FuseMode implements StringIdentifiable {
        NORMAL_FUSE, DETONATION_FUSE;

        public final static Codec<FuseMode> CODEC = StringIdentifiable.createCodec(FuseMode::values);

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }
}
