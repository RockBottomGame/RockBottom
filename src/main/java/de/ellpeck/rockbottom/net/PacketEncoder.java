package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<IPacket> {

    public static int packetsSent;

    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket packet, ByteBuf buf) throws RuntimeException {
        int id = Registries.PACKET_REGISTRY.getId(packet.getClass());

        if (id >= 0) {
            buf.writeByte(id);

            try {
                packet.toBuffer(buf);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't write packet " + packet.getClass() + " with id " + id + " to buffer", e);
            }
        } else {
            throw new IllegalStateException("Found unregistered packet " + packet.getClass());
        }

        packetsSent++;
    }
}
