package aqario.deathkeeper.common.network.listener;

import aqario.deathkeeper.common.network.packet.s2c.OpenGraveScreenS2CPacket;

public interface DeathkeeperClientPlayPacketListener {

    void onOpenGraveScreen(OpenGraveScreenS2CPacket packet);
}
