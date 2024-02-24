package aqario.deathkeeper.common.network;

import aqario.deathkeeper.common.network.packet.s2c.OpenGraveScreenS2CPacket;
import aqario.deathkeeper.server.network.DeathkeeperServerPlayNetworkHandler;

public class DeathkeeperMessages {

    public static void init() {
        DeathkeeperServerPlayNetworkHandler.registerMessage(OpenGraveScreenS2CPacket.class, "open_grave");
    }
}
