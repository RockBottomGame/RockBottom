package de.ellpeck.game.net.client;

import de.ellpeck.game.net.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientNetworkHandler extends SimpleChannelInboundHandler<IPacket>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket packet) throws Exception{
        packet.handle();
    }
}
