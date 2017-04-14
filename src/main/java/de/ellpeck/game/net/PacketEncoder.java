package de.ellpeck.game.net;

import de.ellpeck.game.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<IPacket>{

    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket packet, ByteBuf buf) throws Exception{
        buf.writeByte(NetHandler.PACKET_REGISTRY.getId(packet.getClass()));

        packet.toBuffer(buf);
    }
}
