package aqario.deathkeeper.mixin;

import aqario.deathkeeper.common.Deathkeeper;
import aqario.deathkeeper.common.config.DeathkeeperConfig;
import aqario.deathkeeper.common.entity.GraveEntity;
import aqario.deathkeeper.common.integration.TrinketsIntegration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    @Final
    private Inventory inventory;

    @Shadow
    protected abstract void destroyVanishingCursedItems();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void deathkeeper$dropInventory(CallbackInfo ci) {
        if(!DeathkeeperConfig.enableGraves) {
            return;
        }
        if(!this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            this.destroyVanishingCursedItems();
            if(!this.inventory.isEmpty()
                || this.hasTrinkets()
            ) {
                GraveEntity grave = GraveEntity.create(Player.class.cast(this));
                this.level().addFreshEntity(grave);
                ci.cancel();
            }
        }
    }

    @Unique
    private boolean hasTrinkets() {
        if(Deathkeeper.isTrinketsLoaded()) {
            return TrinketsIntegration.hasTrinkets(this);
        }
        return false;
    }
}
