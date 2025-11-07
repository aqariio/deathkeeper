package aqario.deathkeeper.mixin;

import aqario.deathkeeper.common.entity.GraveEntity;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @ModifyArg(method = "openHandledScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"), index = 0)
    private Packet<?> deathkeeper$inject(Packet<?> packet, @Local ScreenHandler screenHandler, @Local(argsOnly = true) @Nullable NamedScreenHandlerFactory factory) {
        if(factory instanceof GraveEntity grave) {
            return new OpenScreenS2CPacket(screenHandler.syncId, screenHandler.getType(), Text.translatable("container.deathkeeper.grave", grave.getCustomName()));
        }
        return packet;
    }
}
