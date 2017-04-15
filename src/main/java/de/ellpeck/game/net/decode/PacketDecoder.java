package de.ellpeck.game.net.decode;

import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.newdawn.slick.util.Log;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception{
        byte id = buf.readByte();

        Class<? extends IPacket> packetClass = NetHandler.PACKET_REGISTRY.get(id);
        if(packetClass != null){
            IPacket packet = packetClass.newInstance();

            packet.fromBuffer(buf);
            out.add(packet);
        }
        else{
            Log.error("Found unknown packet with id "+id);
        }
    }
}
