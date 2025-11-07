package aqario.deathkeeper.common.screen;

import aqario.deathkeeper.common.entity.GraveEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class GraveScreenHandler extends ChestMenu {
    public final GraveEntity grave;

    public GraveScreenHandler(int syncId, Inventory playerInventory, Container inventory, GraveEntity grave) {
        super(MenuType.GENERIC_9x6, syncId, playerInventory, inventory, 6);
        this.grave = grave;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.grave.isAlive() && player.distanceToSqr(this.grave) <= 64;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.getContainer().stopOpen(player);
        if(this.getContainer().isEmpty()) {
            this.grave.discard();
        }
    }
}
