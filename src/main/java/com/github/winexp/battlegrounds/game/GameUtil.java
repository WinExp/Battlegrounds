package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.FileUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.nio.file.Path;
import java.util.Collections;

public class GameUtil {
    public static void createDeleteWorldTmpFile(Path savePath) {
        FileUtil.writeString(Constants.DELETE_WORLD_TMP_FILE_PATH, savePath.toString());
    }

    public static void deleteWorld() {
        Path savePath = Path.of(FileUtil.readString(Constants.DELETE_WORLD_TMP_FILE_PATH).trim());
        FileUtil.delete(savePath, false,
                GameManager.PERSISTENT_STATE_ID + ".dat", "stats");
    }

    public static void spawnWinnerFireworks(ServerPlayerEntity player, int amount, double offset) {
        World world = player.getWorld();
        Random random = player.getRandom();
        for (int i = 0; i < amount; i++) {
            double xOffset = (random.nextFloat() * offset * 2) - offset;
            double x = player.getX() + 0.5 + xOffset;
            double y = player.getRandomBodyY();
            double zOffset = (random.nextFloat() * offset * 2) - offset;
            double z = player.getZ() + 0.5 + zOffset;
            int flightTime = random.nextBetween(2, 3);
            FireworkExplosionComponent.Type shape = FireworkExplosionComponent.Type.byId(random.nextBetween(0, 4));
            IntList colors = new IntArrayList();
            for (int j = 0; j < random.nextBetween(3, 4); j++) {
                int idx = random.nextInt(DyeColor.values().length);
                colors.add(DyeColor.values()[idx].getFireworkColor());
            }
            IntList fadeColors = new IntArrayList();
            for (int j = 0; j < random.nextBetween(2, 3); j++) {
                int idx = random.nextInt(DyeColor.values().length);
                fadeColors.add(DyeColor.values()[idx].getFireworkColor());
            }
            boolean hasTrail = random.nextBoolean();
            boolean hasTwinkle = random.nextBoolean();
            FireworkExplosionComponent explosion = new FireworkExplosionComponent(shape, colors, fadeColors, hasTrail, hasTwinkle);
            ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
            stack.applyChanges(ComponentChanges.builder()
                    .add(DataComponentTypes.FIREWORKS, new FireworksComponent(flightTime, Collections.singletonList(explosion)))
                    .build());
            FireworkRocketEntity firework = new FireworkRocketEntity(world, x, y, z, stack);
            firework.noClip = true;
            world.spawnEntity(firework);
        }
    }
}
