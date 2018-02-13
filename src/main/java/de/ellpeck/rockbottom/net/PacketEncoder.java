package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.logging.Level;

public class PacketEncoder extends MessageToByteEncoder<IPacket>{

    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket packet, ByteBuf buf) throws Exception{
        int id = RockBottomAPI.PACKET_REGISTRY.getId(packet.getClass());

        if(id >= 0){
            buf.writeByte(id);

            try{
                packet.toBuffer(buf);
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't write packet "+packet.getClass()+" with id "+id+" to buffer", e);
            }
        }
        else{
            RockBottomAPI.logger().warning("Found unregistered packet "+packet.getClass());
        }
    }
}
