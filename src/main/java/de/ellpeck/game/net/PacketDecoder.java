package de.ellpeck.game.net;

import de.ellpeck.game.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception{
        byte id = buf.readByte();

        Class<? extends IPacket> packetClass = NetHandler.PACKET_REGISTRY.get(id);
        IPacket packet = packetClass.newInstance();

        packet.fromBuffer(buf);
        out.add(packet);
    }
}
