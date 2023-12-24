package aqario.deathwriter.common.network.listener;

import aqario.deathwriter.common.network.packet.s2c.OpenGraveScreenS2CPacket;

public interface DeathwriterClientPlayPacketListener {

    void onOpenGraveScreen(OpenGraveScreenS2CPacket packet);
}
