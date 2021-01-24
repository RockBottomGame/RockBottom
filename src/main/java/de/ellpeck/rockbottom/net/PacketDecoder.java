package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        int id = buf.readByte();

        IPacket.IFactory factory = Registries.PACKET_REGISTRY.get(id);
        if (factory != null) {
            IPacket packet = factory.create();
            try {
                packet.fromBuffer(buf);

                if (buf.isReadable()) {
                    ctx.fireExceptionCaught(new IllegalStateException("Packet " + packet.getClass() + " with id " + id + " read from buffer, but left " + buf.readableBytes() + " bytes behind!"));
                }
            } catch (Exception e) {
                ctx.fireExceptionCaught(new RuntimeException("Couldn't read packet " + packet.getClass() + " with id " + id + " from buffer", e));
            }

            out.add(packet);
        } else {
            ctx.fireExceptionCaught(new IllegalStateException("Found unknown packet with id " + id));
        }
    }
}
