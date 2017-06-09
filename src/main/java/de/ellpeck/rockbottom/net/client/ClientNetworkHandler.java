package de.ellpeck.rockbottom.net.client;

import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.RockBottom;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientNetworkHandler extends SimpleChannelInboundHandler<IPacket>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket packet) throws Exception{
        packet.handle(RockBottom.get(), ctx);
    }
}
