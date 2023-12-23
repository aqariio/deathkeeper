package aqario.deathwriter.common.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class GraveScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public GraveScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(54));
    }

    public GraveScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_3X3, syncId);
        checkSize(inventory, 54);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        for(int y = 0; y < 6; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(inventory, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }

        // Player Inventory
        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 139 + y * 18));
            }
        }

        // Player Hotbar
        for(int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 197));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickTransfer(PlayerEntity player, int fromIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(fromIndex);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (fromIndex < 9) {
                if (!this.insertItem(itemStack2, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }
}
