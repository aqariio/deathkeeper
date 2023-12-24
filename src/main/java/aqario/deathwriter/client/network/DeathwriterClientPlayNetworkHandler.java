package aqario.deathwriter.client.network;

import aqario.deathwriter.client.gui.screen.GraveScreen;
import aqario.deathwriter.common.entity.GraveEntity;
import aqario.deathwriter.common.network.listener.DeathwriterClientPlayPacketListener;
import aqario.deathwriter.common.network.packet.s2c.OpenGraveScreenS2CPacket;
import aqario.deathwriter.common.screen.GraveScreenHandler;
import net.minecraft.client.MinecraftClient;

public class DeathwriterClientPlayNetworkHandler implements DeathwriterClientPlayPacketListener {
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
