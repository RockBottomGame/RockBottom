package de.ellpeck.rockbottom.net.decode;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.newdawn.slick.util.Log;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception{
        int id = buf.readByte();

        Class<? extends IPacket> packetClass = RockBottomAPI.PACKET_REGISTRY.get(id);
        if(packetClass != null){
            IPacket packet = packetClass.newInstance();

            try{
                packet.fromBuffer(buf);
            }
            catch(Exception e){
                Log.error("Couldn't read packet "+packetClass+" with id "+id+" from buffer", e);
            }

            out.add(packet);
        }
        else{
            Log.error("Found unknown packet with id "+id);
        }
    }
}
