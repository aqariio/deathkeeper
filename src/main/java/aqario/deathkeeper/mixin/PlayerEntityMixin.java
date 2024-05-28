package aqario.deathkeeper.mixin;

import aqario.deathkeeper.common.entity.GraveEntity;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    @Final
    private PlayerInventory inventory;

    @Shadow
    protected abstract void vanishCursedItems();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    private void deathkeeper$dropInventory(CallbackInfo ci) {
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            this.vanishCursedItems();
            if (!this.inventory.isEmpty() || (TrinketsApi.getTrinketComponent(this).isPresent() && !TrinketsApi.getTrinketComponent(this).get().getAllEquipped().isEmpty())) {
                GraveEntity grave = GraveEntity.create(PlayerEntity.class.cast(this));
                this.getWorld().spawnEntity(grave);
                ci.cancel();
            }
        }
    }
}
