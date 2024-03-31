package com.github.winexp.battlegrounds.helper;

import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderStage;
import org.jetbrains.annotations.NotNull;

public class WorldHelper {
    private final World world;
    private final WorldBorder border;

    public WorldHelper(@NotNull World world) {
        this.world = world;
        this.border = world.getWorldBorder();
    }

    public World getWorld() {
        return this.world;
    }

    public void setDefaultBorder() {
        this.border.load(WorldBorder.DEFAULT_BORDER);
    }

    public WorldBorder getBorder() {
        return this.border;
    }

    public int getBorderSize() {
        return (int) this.border.getSize();
    }

    public void setBorderSize(int size) {
        this.border.setSize(size);
    }

    public int getBorderCenterX() {
        return (int) this.border.getCenterX();
    }

    public int getBorderCenterZ() {
        return (int) this.border.getCenterZ();
    }

    public WorldBorderStage getBorderStage() {
        return this.border.getStage();
    }

    public void interpolateBorderSize(int fromSize, int toSize, long timeMillis) {
        this.border.interpolateSize(fromSize, toSize, timeMillis);
    }

    public void setBorderSize(int size, long timeMillis) {
        this.border.interpolateSize(this.border.getSize(), size, timeMillis);
    }

    public void setBorderCenter(int x, int z) {
        this.border.setCenter(x, z);
    }
}
