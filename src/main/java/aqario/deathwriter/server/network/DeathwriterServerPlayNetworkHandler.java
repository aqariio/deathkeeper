package aqario.deathwriter.server.network;

import aqario.deathwriter.client.network.DeathwriterClientPlayNetworkHandler;
import aqario.deathwriter.common.Deathwriter;
import aqario.deathwriter.common.network.DeathwriterPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeathwriterServerPlayNetworkHandler {
    private static final Map<Class<?>, Identifier> identifiers = new HashMap<>();

    private static Identifier getMessageIdentifier(DeathwriterPacket packet) {
        return Objects.requireNonNull(identifiers.get(packet.getClass()), "Used unregistered message!");
    }

    public static <T extends DeathwriterPacket> void registerMessage(Class<T> packet, String id) {
        Identifier identifier = new Identifier(Deathwriter.ID, id);
        identifiers.put(packet, identifier);

        ServerPlayNetworking.registerGlobalReceiver(identifier, (server, player, handler, buffer, responder) -> {
            DeathwriterPacket p = DeathwriterPacket.decode(buffer);
            server.execute(() -> p.apply(new DeathwriterClientPlayNetworkHandler()));
        });

        if (MinecraftQuiltLoader.getEnvironmentType() == EnvType.CLIENT) {
            ClientProxy.register(identifier);
        }
    }

    public static void sendPacket(ServerPlayerEntity player, DeathwriterPacket packet) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        packet.encode(buffer);
        ServerPlayNetworking.send(player, getMessageIdentifier(packet), buffer);
    }

    private static final class ClientProxy {
        private ClientProxy() {
            throw new RuntimeException("new ClientProxy()");
        }

        public static void register(Identifier identifier) {
            ClientPlayNetworking.registerGlobalReceiver(identifier, (client, ignore1, buffer, ignore2) -> {
                DeathwriterPacket p = DeathwriterPacket.decode(buffer);
                client.execute(() -> p.apply(new DeathwriterClientPlayNetworkHandler()));
            });
        }
    }
}
