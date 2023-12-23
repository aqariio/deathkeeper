package aqario.deathwriter.mixin;

import aqario.deathwriter.common.entity.GraveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow
    @Final
    public PlayerEntity player;

    @Shadow public abstract boolean isEmpty();

    @Overwrite
    public void dropAll() {
        if (player instanceof ServerPlayerEntity serverPlayerEntity && !isEmpty()) {
            GraveEntity grave = GraveEntity.create(serverPlayerEntity);
            player.getWorld().spawnEntity(grave);
        }
    }
}