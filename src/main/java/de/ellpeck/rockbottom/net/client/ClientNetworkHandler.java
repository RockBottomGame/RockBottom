package de.ellpeck.rockbottom.net.client;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.newdawn.slick.util.Log;

public class ClientNetworkHandler extends SimpleChannelInboundHandler<IPacket>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket packet) throws Exception{
        try{
            packet.handle(RockBottom.get(), ctx);
        }
        catch(Exception e){
            Log.error("Couldn't handle packet on client", e);
        }
    }
}
