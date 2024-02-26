package com.github.winexp.battlegrounds.entity.projectile;

import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.util.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class FlashBangEntity extends ThrownItemEntity {
    public final static float MAX_FLASH_TICKS = 80;
    public final static int MAX_DISTANCE = 32;
    private int fuse = 0;

    public FlashBangEntity(net.minecraft.entity.EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public FlashBangEntity(World world, double d, double e, double f, int fuse) {
        super(EntityTypes.FLASH_BANG, d, e, f, world);
        this.fuse = fuse;
    }

    public FlashBangEntity(LivingEntity livingEntity, World world, int fuse) {
        super(EntityTypes.FLASH_BANG, livingEntity, world);
        this.fuse = fuse;
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return (itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    @Override
    public void tick() {
        if (fuse >= 0) {
            this.fuse--;
            if (fuse <= 0) {
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

            for(int i = 0; i < 8; ++i) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("fuse")) this.fuse = nbt.getInt("fuse");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("fuse", this.fuse < 0 ? -1 : this.fuse);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 1.0F);
    }

    private float distanceToPlayer(PlayerEntity player) {
        float f = (float)(this.getX() - player.getX());
        float g = (float)(this.getY() - player.getEyeY());
        float h = (float)(this.getZ() - player.getZ());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    private void sendFlash() {
        List<? extends PlayerEntity> players = this.getWorld().getPlayers();
        for (PlayerEntity player1 : players) {
            ServerPlayerEntity player = (ServerPlayerEntity) player1;
            float distance = this.distanceToPlayer(player);
            if (distance <= MAX_DISTANCE) {
                PacketByteBuf buf = PacketByteBufs.create();
                float strength = (MAX_DISTANCE - distance) / MAX_DISTANCE;
                buf.writeFloat(strength);
                buf.writeVec3d(this.getPos());
                ServerPlayNetworking.send(player, Constants.FLASH_BANG_PACKET_ID, buf);
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte) 3);
            this.sendFlash();
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FLASH_BANG;
    }
}
