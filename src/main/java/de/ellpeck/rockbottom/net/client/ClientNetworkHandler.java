package de.ellpeck.rockbottom.net.client;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;

public class ClientNetworkHandler extends SimpleChannelInboundHandler<IPacket>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket packet) throws Exception{
        try{
            packet.handle(RockBottomAPI.getGame(), ctx);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't handle packet on client", e);
        }
    }
}
