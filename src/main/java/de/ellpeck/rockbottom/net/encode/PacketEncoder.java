package de.ellpeck.rockbottom.net.encode;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.newdawn.slick.util.Log;

public class PacketEncoder extends MessageToByteEncoder<IPacket>{

    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket packet, ByteBuf buf) throws Exception{
        int id = RockBottomAPI.PACKET_REGISTRY.getId(packet.getClass());

        if(id >= 0){
            buf.writeByte(id);
            packet.toBuffer(buf);
        }
        else{
            Log.error("Found unregistered packet "+packet.getClass());
        }
    }
}
