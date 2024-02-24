package aqario.deathkeeper.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccessor {
    @Accessor
    int getScreenHandlerSyncId();

    @Invoker
    void callOnSpawn(ScreenHandler screenHandler);

    @Invoker
    void callIncrementScreenHandlerSyncId();
}
