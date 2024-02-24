package aqario.deathkeeper.common.network.packet.s2c;

import aqario.deathkeeper.common.network.DeathkeeperPacket;
import aqario.deathkeeper.common.network.listener.DeathkeeperClientPlayPacketListener;

public class OpenGraveScreenS2CPacket implements DeathkeeperPacket {
    private final int syncId;
    private final int standId;

    public OpenGraveScreenS2CPacket(int syncId, int standId) {
        this.syncId = syncId;
        this.standId = standId;
    }

    @Override
    public void apply(DeathkeeperClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onOpenGraveScreen(this);
    }

    public int getSyncId() {
        return this.syncId;
    }

    public int getStandId() {
        return this.standId;
    }
}
