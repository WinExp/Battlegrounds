package com.github.winexp.battlegrounds.block.entity;

import com.github.winexp.battlegrounds.component.DataComponentTypes;
import com.github.winexp.battlegrounds.component.SoakComponent;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.registry.tag.ModItemTags;
import com.github.winexp.battlegrounds.screen.SoakTableScreenHandler;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.Objects;

public class SoakTableBlockEntity extends LockableContainerBlockEntity {
    private static final double DURATION_MULTIPLIER = 1.0 / 8 / 12;
    private static final Text NAME = Text.translatable("container.battlegrounds.soak_table");
    private static final int SOAK_TIME = 2 * 20;
    private static final int FUEL_PROPERTY_INDEX = 0;
    private static final int SOAK_TIME_PROPERTY_INDEX = 1;
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int POTION_SLOT_INDEX = 1;
    public static final int FUEL_SLOT_INDEX = 2;
    private static final int MAX_FUEL = 3;
    private static final int FUEL_PER_ITEM = 1;

    private int soakTime;
    private int fuel;
    private DefaultedList<ItemStack> inventory;
    private final PropertyDelegate propertyDelegate;

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
        return new SoakTableScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        this.soakTime = nbt.getShort("soak_time");
        this.fuel = nbt.getByte("fuel");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        nbt.putShort("soak_time", (short) this.soakTime);
        nbt.putByte("fuel", (byte) this.fuel);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == INPUT_SLOT_INDEX) return stack.isIn(ModItemTags.SOAKABLE);
        else if (slot == POTION_SLOT_INDEX) return stack.isOf(Items.POTION);
        else if (slot == FUEL_SLOT_INDEX) return stack.isOf(Items.GUNPOWDER);
        else return false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SoakTableBlockEntity blockEntity) {
        ItemStack fuelStack = blockEntity.inventory.get(FUEL_SLOT_INDEX);
        ItemStack inputStack = blockEntity.inventory.get(INPUT_SLOT_INDEX);
        if (blockEntity.fuel + FUEL_PER_ITEM <= MAX_FUEL && fuelStack.isOf(Items.GUNPOWDER)) {
            int amount = Math.min((MAX_FUEL - blockEntity.fuel) / FUEL_PER_ITEM, fuelStack.getCount());
            blockEntity.fuel += amount * FUEL_PER_ITEM;
            fuelStack.decrement(amount);
        }

        boolean canCraft = canCraft(blockEntity.inventory);
        if (blockEntity.soakTime > 0) {
            blockEntity.soakTime--;
            if (blockEntity.soakTime == 0 && canCraft) {
                blockEntity.fuel--;
                craft(world, pos, blockEntity.inventory);
                markDirty(world, pos, state);
            } else if (!canCraft && inputStack.isIn(ModItemTags.SOAKABLE)) {
                blockEntity.soakTime = 0;
                markDirty(world, pos, state);
            }
        } else if (canCraft && blockEntity.fuel > 0) {
            blockEntity.soakTime = SOAK_TIME;
            markDirty(world, pos, state);
        }
    }

    private static void craft(World world, BlockPos pos, DefaultedList<ItemStack> slots) {
        ItemStack potionStack = slots.get(POTION_SLOT_INDEX);
        ItemStack inputStack = slots.get(INPUT_SLOT_INDEX);
        SoakComponent.Builder builder = inputStack.contains(DataComponentTypes.IMMERSE_DATA)
                ? new SoakComponent.Builder(SoakComponent.SoakType.IMMERSE, DURATION_MULTIPLIER, Objects.requireNonNull(inputStack.get(DataComponentTypes.IMMERSE_DATA)))
                : new SoakComponent.Builder(SoakComponent.SoakType.IMMERSE, DURATION_MULTIPLIER);
        builder.addAll(Objects.requireNonNull(potionStack.get(DataComponentTypes.POTION_CONTENTS)).getEffects());
        inputStack.set(DataComponentTypes.IMMERSE_DATA, builder.build());
        slots.set(INPUT_SLOT_INDEX, inputStack);
        slots.set(POTION_SLOT_INDEX, new ItemStack(Items.GLASS_BOTTLE));
        world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public static boolean canCraft(DefaultedList<ItemStack> inventory) {
        ItemStack inputStack = inventory.get(INPUT_SLOT_INDEX);
        ItemStack potionStack = inventory.get(POTION_SLOT_INDEX);
        boolean immerseCheckFlag = inputStack.contains(DataComponentTypes.IMMERSE_DATA);
        MutableBoolean isPotionInstant = new MutableBoolean(false);
        MutableBoolean isPotionAlready = new MutableBoolean(false);
        if (potionStack.contains(DataComponentTypes.POTION_CONTENTS)) {
            Objects.requireNonNull(potionStack.get(DataComponentTypes.POTION_CONTENTS)).forEachEffect(effect -> {
                if (effect.getEffectType().value().isInstant()) isPotionInstant.setValue(true);

                if (immerseCheckFlag) {
                    SoakComponent component = inputStack.get(DataComponentTypes.IMMERSE_DATA);
                    assert component != null;
                    if (component.contains(effect)) isPotionAlready.setValue(true);
                }
            });
        } else return false;
        return inputStack.isIn(ModItemTags.SOAKABLE)
                && potionStack.isOf(Items.POTION) && !isPotionInstant.getValue() && !isPotionAlready.getValue();
    }
}
