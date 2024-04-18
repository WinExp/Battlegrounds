package com.github.winexp.battlegrounds.entity.projectile.thrown;

import com.github.winexp.battlegrounds.entity.data.ModTrackedDataHandlers;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
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
import java.util.Queue;

public abstract class AbstractThrownPropEntity extends ThrownItemEntity {
    private static final byte STATUS_DISCARD_PARTICLES = 3;
    private static final TrackedData<Integer> FUSE = DataTracker.registerData(AbstractThrownPropEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<FuseMode> FUSE_MODE = DataTracker.registerData(AbstractThrownPropEntity.class, ModTrackedDataHandlers.PROP_THROWN_FUSE_MODE);

    public AbstractThrownPropEntity(EntityType<? extends AbstractThrownPropEntity> entityType, World world) {
        super(entityType, world);
    }

    public AbstractThrownPropEntity(EntityType<? extends AbstractThrownPropEntity> entityType, double d, double e, double f, World world) {
        super(entityType, d, e, f, world);
    }

    public AbstractThrownPropEntity(EntityType<? extends AbstractThrownPropEntity> entityType, LivingEntity livingEntity, World world) {
        super(entityType, livingEntity, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(FUSE, 0);
        this.getDataTracker().startTracking(FUSE_MODE, FuseMode.NORMAL_FUSE);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getFuse() >= 0 && !this.isRemoved()) {
            this.setFuse(this.getFuse() - 1);
            if (this.getFuse() <= 0) {
                this.trigger();
            }
        }
    }

    @Override
    public void handleStatus(byte status) {
        if (status == STATUS_DISCARD_PARTICLES) {
            this.spawnTriggerParticles();
        }
        else super.handleStatus(status);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("fuse")) this.setFuse(nbt.getInt("fuse"));
        if (nbt.contains("fuse_status")) this.setFuseMode(FuseMode.CODEC.parse(NbtOps.INSTANCE, nbt.get("fuse_status"))
                .result().orElse(FuseMode.NORMAL_FUSE));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("fuse", this.getFuse() < 0 ? -1 : this.getFuse());
        nbt.put("fuse_status", Util.getResult(FuseMode.CODEC.encodeStart(NbtOps.INSTANCE, this.getFuseMode()), IllegalStateException::new));
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.isReboundable()) {
            this.reboundEntity(entityHitResult.getEntity());
        }
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), this.getEntityReboundDamage());
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.reboundBlock(blockHitResult);
    }

    private void reboundEntity(Entity target) {
        Vec3d velocity = this.getVelocity();
        this.setVelocity(velocity.negate().add(target.getVelocity()).multiply(this.getEntityReboundVelocityMultiplier()));
        if (!this.getWorld().isClient) {
            this.playReboundSound();
        }
    }

    private Vec3d computeBlockReboundVelocity(Vec3d velocity, Direction side, boolean insideBlock) {
        double velocityMultiplier = this.getBlockReboundVelocityMultiplier(insideBlock);
        if (insideBlock) {
            velocity = this.getVelocity().multiply(velocityMultiplier);
        } else {
            double deltaX = side.getAxis() == Direction.Axis.X ? -1 : 1;
            double deltaY = side.getAxis() == Direction.Axis.Y ? -1 : 1;
            double deltaZ = side.getAxis() == Direction.Axis.Z ? -1 : 1;
            velocity = new Vec3d(velocity.x * deltaX, velocity.y * deltaY, velocity.z * deltaZ)
                    .multiply(velocityMultiplier);
        }
        return velocity;
    }

    private void reboundBlock(BlockHitResult hitResult) {
        Queue<BlockHitResult> reboundQueue = new LinkedList<>();
        reboundQueue.add(hitResult);
        while (!reboundQueue.isEmpty()) {
            BlockHitResult blockHitResult = reboundQueue.poll();
            Vec3d velocity = this.computeBlockReboundVelocity(this.getVelocity(), blockHitResult.getSide(), blockHitResult.isInsideBlock());
            this.refreshPositionAfterTeleport(blockHitResult.getPos());
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
            if (speed <= this.getTriggerThresholdSpeed() && blockHitResult.getSide() == Direction.UP) {
                int detonationFuse = this.getDefaultDetonationFuse();
                this.refreshPositionAfterTeleport(blockHitResult.getPos());
                velocity = Vec3d.ZERO;
                this.setNoGravity(true);
                this.noClip = true;
                this.setFuseMode(AbstractThrownPropEntity.FuseMode.DETONATION_FUSE);
                this.setFuse(Math.min(this.getFuse() < 0 ? detonationFuse
                        : this.getFuse(), detonationFuse));
                reboundQueue.clear();
            }
            this.setVelocity(velocity);
            if (!this.getWorld().isClient) {
                this.playReboundSound();
            }
        }
    }

    private void trigger() {
        World world = this.getWorld();
        if (!world.isClient) {
            world.sendEntityStatus(this, STATUS_DISCARD_PARTICLES);
            this.playTriggerSound();
            this.onTrigger();
            this.discard();
        }
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return (itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
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

    public int getDefaultDetonationFuse() {
        return 10;
    }

    public double getTriggerThresholdSpeed() {
        return 0.1;
    }

    public boolean isReboundable() {
        return true;
    }

    public double getBlockReboundVelocityMultiplier(boolean insideBlock) {
        if (insideBlock) return 1.05;
        else return 0.4;
    }
    
    public double getEntityReboundVelocityMultiplier() {
        return 0.15;
    }

    public float getEntityReboundDamage() {
        return 1.0F;
    }

    protected void spawnTriggerParticles() {
        ParticleEffect particleEffect = this.getParticleParameters();
        for (int i = 0; i < 8; ++i) {
            this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    protected void playTriggerSound() {
        this.playSound(SoundEvents.ENTITY_PROP_GENERIC_TRIGGER, 2, 1.0F);
    }

    protected void playReboundSound() {
        Random random = this.getWorld().getRandom();
        this.playSound(SoundEvents.ENTITY_PROP_GENERIC_REBOUND, 1.5F, RandomUtil.nextFloatBetween(random, 0.95F, 1.05F));
    }

    protected abstract void onTrigger();

    public enum FuseMode implements StringIdentifiable {
        NORMAL_FUSE, DETONATION_FUSE;

        public final static Codec<FuseMode> CODEC = StringIdentifiable.createCodec(FuseMode::values);

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }
}