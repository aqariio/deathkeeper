package aqario.deathkeeper.common.screen;

import aqario.deathkeeper.common.entity.GraveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class GraveScreenHandler extends GenericContainerScreenHandler {
    public final GraveEntity grave;

    public GraveScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, GraveEntity grave) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inventory, 6);
        this.grave = grave;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.grave.isAlive() && player.squaredDistanceTo(this.grave) <= 64;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.getInventory().onClose(player);
        if(this.getInventory().isEmpty()) {
            this.grave.discard();
        }
    }
}
