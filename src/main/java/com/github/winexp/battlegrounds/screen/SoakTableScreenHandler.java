package com.github.winexp.battlegrounds.screen;

import com.github.winexp.battlegrounds.block.entity.SoakTableBlockEntity;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.registry.tag.ModItemTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class SoakTableScreenHandler extends ScreenHandler {
    private static final int INVENTORY_SIZE = 3;
    private static final int PROPERTY_COUNT = 2;

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public SoakTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(INVENTORY_SIZE), new ArrayPropertyDelegate(PROPERTY_COUNT));
    }

    public SoakTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenHandlerType.SOAK, syncId);
        checkSize(inventory, INVENTORY_SIZE);
        checkDataCount(propertyDelegate, PROPERTY_COUNT);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addSlot(new InputSlot(inventory, SoakTableBlockEntity.INPUT_SLOT_INDEX, 79, 17));
        this.addSlot(new PotionSlot(inventory, SoakTableBlockEntity.POTION_SLOT_INDEX, 79, 58));
        this.addSlot(new FuelSlot(inventory, SoakTableBlockEntity.FUEL_SLOT_INDEX, 17, 17));
        this.addProperties(propertyDelegate);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotId) {
        Slot slot = this.slots.get(slotId);
        if (slot.hasStack()) {
            ItemStack newStack = slot.getStack();
            ItemStack originStack = newStack.copy();
            if (slotId < 0 || slotId >= INVENTORY_SIZE) {
                if (InputSlot.matches(originStack)) {
                    if (this.insertItem(newStack, SoakTableBlockEntity.INPUT_SLOT_INDEX, SoakTableBlockEntity.INPUT_SLOT_INDEX + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (PotionSlot.matches(originStack)) {
                    if (this.insertItem(newStack, SoakTableBlockEntity.POTION_SLOT_INDEX, SoakTableBlockEntity.POTION_SLOT_INDEX + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (FuelSlot.matches(originStack)) {
                    if (this.insertItem(newStack, SoakTableBlockEntity.FUEL_SLOT_INDEX, SoakTableBlockEntity.FUEL_SLOT_INDEX + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotId >= INVENTORY_SIZE && slotId < INVENTORY_SIZE + 27) {
                    if (!this.insertItem(newStack, INVENTORY_SIZE, INVENTORY_SIZE + 27, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotId >= INVENTORY_SIZE + 27 && slotId <= INVENTORY_SIZE + 27 + 9) {
                    if (!this.insertItem(newStack, INVENTORY_SIZE + 27, INVENTORY_SIZE + 27 + 9, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                if (!this.insertItem(newStack, INVENTORY_SIZE, INVENTORY_SIZE + 27 + 9, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(newStack, originStack);
            }

            if (newStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
                slot.markDirty();
            }

            if (newStack.getCount() == originStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, newStack);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    static class InputSlot extends Slot {
        public InputSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return matches(stack);
        }

        public static boolean matches(ItemStack stack) {
            return stack.isIn(ModItemTags.SOAKABLE);
        }
    }

    static class PotionSlot extends Slot {
        public PotionSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return matches(stack);
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }

        public static boolean matches(ItemStack stack) {
            return stack.isOf(Items.POTION);
        }
    }

    static class FuelSlot extends Slot {
        public FuelSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return matches(stack);
        }

        public static boolean matches(ItemStack stack) {
            return stack.isOf(Items.GUNPOWDER);
        }
    }
}
