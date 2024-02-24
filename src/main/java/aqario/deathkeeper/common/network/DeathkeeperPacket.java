package aqario.deathkeeper.common.network;

import aqario.deathkeeper.common.network.listener.DeathkeeperClientPlayPacketListener;
import net.minecraft.network.PacketByteBuf;

import java.io.*;

public interface DeathkeeperPacket extends Serializable {
    static DeathkeeperPacket decode(PacketByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (DeathkeeperPacket)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("SneakyThrows", e);
        }
    }

    default void encode(PacketByteBuf buf) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException("SneakyThrows", e);
        }

        buf.writeBytes(baos.toByteArray());
    }

    void apply(DeathkeeperClientPlayPacketListener clientPlayPacketListener);
}
