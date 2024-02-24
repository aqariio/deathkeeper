package aqario.deathkeeper.client.network;

import aqario.deathkeeper.client.gui.screen.GraveScreen;
import aqario.deathkeeper.common.entity.GraveEntity;
import aqario.deathkeeper.common.network.listener.DeathkeeperClientPlayPacketListener;
import aqario.deathkeeper.common.network.packet.s2c.OpenGraveScreenS2CPacket;
import aqario.deathkeeper.common.screen.GraveScreenHandler;
import net.minecraft.client.MinecraftClient;

public class DeathkeeperClientPlayNetworkHandler implements DeathkeeperClientPlayPacketListener {
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onOpenGraveScreen(OpenGraveScreenS2CPacket packet) {
        if (client.world != null && client.player != null) {
            GraveEntity grave = (GraveEntity) client.world.getEntityById(packet.getStandId());
            if (grave != null) {
                GraveScreenHandler screenHandler = (GraveScreenHandler)grave.createMenu(packet.getSyncId(), client.player.getInventory(), client.player);
                GraveScreen screen = new GraveScreen(screenHandler, client.player.getInventory(), grave);
                client.player.currentScreenHandler = screen.getScreenHandler();
                client.setScreen(screen);
            }
        }
    }
}
