package com.github.winexp.battlegrounds.item.thrown;

import com.github.winexp.battlegrounds.entity.projectile.thrown.FlashBangEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FlashBangItem extends Item {
    public static final int FUSE = 30;
    private static final int COOLDOWN = 20;

    public FlashBangItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!world.isClient) {
            user.getItemCooldownManager().set(this, COOLDOWN);
            FlashBangEntity entity = new FlashBangEntity(user, world);
            entity.setItem(stack);
            entity.setFuse(FUSE);
            entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
            world.spawnEntity(entity);
        }
        if (!user.getAbilities().creativeMode) stack.decrement(1);
        return TypedActionResult.success(stack, world.isClient);
    }
}
