package com.github.winexp.battlegrounds.entity.projectile;

import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.network.packet.s2c.play.FlashS2CPacket;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import com.github.winexp.battlegrounds.util.WorldUtil;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class FlashBangEntity extends ThrownItemEntity {
    public static final int MAX_DISTANCE = 48;
    public static final float DISTANCE_INCREMENT = 15;
    private static final TrackedData<Integer> FUSE = DataTracker.registerData(FlashBangEntity.class, TrackedDataHandlerRegistry.INTEGER);

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
            this.discard();
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
                this.flashAndDiscard();
            }
        }
        double speed = Math.sqrt(Math.pow(this.getVelocity().x, 2) + Math.pow(this.getVelocity().y, 2) + Math.pow(this.getVelocity().z, 2));
        if (speed <= 0.1F && (WorldUtil.isFullCube(this.getWorld(), this.getBlockPos())
                || WorldUtil.isFullCube(this.getWorld(), this.getBlockPos().down()))) {
            this.flashAndDiscard();
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
        this.reboundEntity();
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 2.0F);
    }

    private void reboundEntity() {
        Vec3d velocity = this.getVelocity();
        this.setVelocity(-velocity.x * 0.2,
                -velocity.y * 0.2,
                -velocity.z * 0.2);
    }

    private void reboundBlock(Direction side) {
        Vec3d velocity = this.getVelocity();
        int deltaX = side.getAxis() == Direction.Axis.X ? -1 : 1;
        int deltaY = side.getAxis() == Direction.Axis.Y ? -1 : 1;
        int deltaZ = side.getAxis() == Direction.Axis.Z ? -1 : 1;
        this.setVelocity(velocity.x * deltaX * 0.4,
                velocity.y * deltaY * 0.4,
                velocity.z * deltaZ * 0.4);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.reboundBlock(blockHitResult.getSide());
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        Random random = this.getWorld().getRandom();
        this.playSound(SoundEvents.ENTITY_FLASH_BANG_REBOUND, 1.0F, RandomUtil.nextFloatBetween(random, 0.95F, 1.05F));
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FLASH_BANG;
    }
}
