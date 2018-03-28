package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.logging.Level;

public class PacketDecoder extends ByteToMessageDecoder{

    public static int packetsReceived;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception{
        int id = buf.readByte();

        Class<? extends IPacket> packetClass = RockBottomAPI.PACKET_REGISTRY.get(id);
        if(packetClass != null){
            IPacket packet = packetClass.getConstructor().newInstance();

            try{
                packet.fromBuffer(buf);

                if(buf.isReadable()){
                    RockBottomAPI.logger().log(Level.WARNING, "Packet "+packetClass+" with id "+id+" read from buffer, but left "+buf.readableBytes()+" bytes behind!");
                    buf.clear();
                }
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't read packet "+packetClass+" with id "+id+" from buffer", e);
                buf.clear();
            }

            out.add(packet);
        }
        else{
            RockBottomAPI.logger().log(Level.WARNING, "Found unknown packet with id "+id);
            buf.clear();
        }

        packetsReceived++;
    }
}
