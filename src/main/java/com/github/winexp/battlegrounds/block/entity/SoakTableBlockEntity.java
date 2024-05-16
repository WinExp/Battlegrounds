package com.github.winexp.battlegrounds.block.entity;

import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.registry.tag.ModItemTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoakTableBlockEntity extends LockableContainerBlockEntity {
    private static final Text NAME = Text.translatable("container.battlegrounds.soak_table");
    private static final int MAX_SOAK_TIME = 15 * 20;
    private static final int FUEL_PROPERTY_INDEX = 0;
    private static final int SOAK_TIME_PROPERTY_INDEX = 1;
    private static final int FUEL_SLOT_INDEX = 0;
    private static final int INPUT_SLOT_INDEX = 1;
    private static final int POTION_SLOT_INDEX = 2;
    private static final int MAX_FUEL = 8;
    private static final int FUEL_PER_ITEM = 4;

    private int soakTime;
    private int fuel;
    private DefaultedList<ItemStack> inventory;
    private PropertyDelegate propertyDelegate;

    public SoakTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SOAK_TABLE, pos, state);
        this.inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case FUEL_PROPERTY_INDEX -> SoakTableBlockEntity.this.fuel;
                    case SOAK_TIME_PROPERTY_INDEX -> SoakTableBlockEntity.this.soakTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case FUEL_PROPERTY_INDEX -> SoakTableBlockEntity.this.fuel = value;
                    case SOAK_TIME_PROPERTY_INDEX -> SoakTableBlockEntity.this.soakTime = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    protected Text getContainerName() {
        return NAME;
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    public static void tick(World world, BlockPos pos, BlockState state, SoakTableBlockEntity blockEntity) {
        ItemStack fuelStack = blockEntity.inventory.get(FUEL_SLOT_INDEX);
        if (blockEntity.fuel + FUEL_PER_ITEM <= MAX_FUEL && fuelStack.isOf(Items.GUNPOWDER)) {
            int amount = Math.min((MAX_FUEL - blockEntity.fuel) / FUEL_PER_ITEM, fuelStack.getCount());
            blockEntity.fuel += amount * FUEL_PER_ITEM;
            fuelStack.decrement(amount);
        }

        boolean canCraft = canCraft(blockEntity.inventory);
        if (blockEntity.soakTime > 0) {
            blockEntity.soakTime--;
            if (blockEntity.soakTime == 0 && canCraft) {
            }
        }
    }

    private static boolean canCraft(DefaultedList<ItemStack> inventory) {
        return inventory.get(INPUT_SLOT_INDEX).isIn(ModItemTags.SOAKABLE)
                && !inventory.get(POTION_SLOT_INDEX).isIn(ConventionalItemTags.POTIONS);
    }
}
