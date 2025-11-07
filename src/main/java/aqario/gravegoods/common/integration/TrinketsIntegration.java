package aqario.gravegoods.common.integration;

import aqario.gravegoods.common.entity.GraveEntity;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrinketsIntegration {
    public static boolean hasTrinkets(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity).isPresent()
            && !TrinketsApi.getTrinketComponent(entity).get().getAllEquipped().isEmpty();
    }

    public static void putTrinketsInGrave(Player player, GraveEntity grave) {
        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
            for(Tuple<SlotReference, ItemStack> slotReferenceItemStackPair : trinkets.getAllEquipped()) {
                grave.items.addItem(slotReferenceItemStackPair.getB());
            }
        });
    }
}
