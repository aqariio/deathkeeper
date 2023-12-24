package aqario.deathwriter.common.network.packet.s2c;

import aqario.deathwriter.common.network.DeathwriterPacket;
import aqario.deathwriter.common.network.listener.DeathwriterClientPlayPacketListener;

public class OpenGraveScreenS2CPacket implements DeathwriterPacket {
    private final int syncId;
    private final int standId;

    public OpenGraveScreenS2CPacket(int syncId, int standId) {
        this.syncId = syncId;
        this.standId = standId;
    }

    @Override
    public void apply(DeathwriterClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onOpenGraveScreen(this);
    }

    public int getSyncId() {
        return this.syncId;
    }

    public int getStandId() {
        return this.standId;
    }
}
