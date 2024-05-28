//package aqario.deathkeeper.mixin;
//
//import aqario.deathkeeper.common.entity.GraveEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.server.network.ServerPlayerEntity;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(value = PlayerInventory.class, priority = 1001)
//public abstract class PlayerInventoryMixin {
//    @Shadow
//    @Final
//    public PlayerEntity player;
//
//    @Shadow public abstract boolean isEmpty();
//
//    @Inject(method = "dropAll", at = @At("HEAD"), cancellable = true)
//    public void dropAll(CallbackInfo ci) {
//        if (player instanceof ServerPlayerEntity serverPlayerEntity && !isEmpty()) {
//            GraveEntity grave = GraveEntity.create(serverPlayerEntity);
//            player.getWorld().spawnEntity(grave);
//            ci.cancel();
//        }
//    }
//}