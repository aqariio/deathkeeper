package aqario.deathwriter.common.network;

import aqario.deathwriter.common.network.packet.s2c.OpenGraveScreenS2CPacket;
import aqario.deathwriter.server.network.DeathwriterServerPlayNetworkHandler;

public class DeathwriterMessages {

    public static void init() {
        DeathwriterServerPlayNetworkHandler.registerMessage(OpenGraveScreenS2CPacket.class, "open_grave");
    }
}
