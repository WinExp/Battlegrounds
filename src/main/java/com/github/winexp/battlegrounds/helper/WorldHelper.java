package com.github.winexp.battlegrounds.helper;

import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.NotNull;

public class WorldHelper {
    private final World world;
    private final WorldBorder border;

    public WorldHelper(@NotNull World world){
        this.world = world;
        this.border = world.getWorldBorder();
    }

    public int getBorderSize(){
        return (int) border.getSize();
    }

    public int getBorderCenterX(){
        return (int) border.getCenterX();
    }

    public int getBorderCenterZ(){
        return (int) border.getCenterZ();
    }

    public void setBorderSize(int size){
        border.setSize(size);
    }
    public void setBorderSize(int size, long timeMillis){
        border.interpolateSize(border.getSize(), size, timeMillis);
    }

    public void setBorderCenter(int x, int z){
        border.setCenter(x, z);
    }
}
