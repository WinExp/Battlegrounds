package com.github.winexp.battlegrounds.item.thrown;

import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.util.TextUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class FlashBangItem extends RangedWeaponItem {
    public final static Predicate<ItemStack> PROJECTILES = (stack) -> stack.isOf(Items.FLASH_BANG);
    public final static float DEFAULT_VELOCITY = 1.3F;
    private final static int DEFAULT_FUSE = 30;

    public FlashBangItem(Settings settings) {
        super(settings);
    }

    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            float progress = getPullProgress(this.getMaxUseTime(stack) - remainingUseTicks);
            if (progress >= 0.15F) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
                if (!world.isClient) {
                    NbtCompound nbt = stack.getNbt();
                    FlashBangEntity entity = new FlashBangEntity(player, world, getFuse(nbt));
                    entity.setItem(stack);
                    entity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, progress * getVelocity(nbt), 1.0F);
                    world.spawnEntity(entity);
                }
                if (!player.getAbilities().creativeMode) stack.decrement(1);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return PROJECTILES;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public int getRange() {
        return 15;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        float fuseSeconds =  (float) getFuse(nbt) / 20;
        float velocity = getVelocity(nbt) * 20;
        tooltip.add(TextUtil.translatableWithColor("item.battlegrounds.flash_bang.fuse", TextUtil.GRAY, fuseSeconds));
        tooltip.add(TextUtil.translatableWithColor("item.battlegrounds.flash_bang.velocity", TextUtil.GRAY, velocity));
    }

    public static int getFuse(NbtCompound nbt) {
        int fuse = DEFAULT_FUSE;
        if (nbt != null && nbt.contains("fuse")) {
            fuse = nbt.getInt("fuse");
        }
        return fuse;
    }

    public static float getVelocity(NbtCompound nbt) {
        float speed = DEFAULT_VELOCITY;
        if (nbt != null && nbt.contains("velocity")) {
            speed = nbt.getFloat("velocity");
        }
        return speed;
    }
}
