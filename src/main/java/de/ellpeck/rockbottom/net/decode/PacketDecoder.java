package de.ellpeck.rockbottom.net.decode;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception{
        int id = buf.readByte();

        Class<? extends IPacket> packetClass = RockBottomAPI.PACKET_REGISTRY.get(id);
        if(packetClass != null){
            IPacket packet = packetClass.newInstance();

            packet.fromBuffer(buf);
            out.add(packet);
        }
        else{
            throw new NullPointerException("Found unknown packet with id "+id);
        }
    }
}
