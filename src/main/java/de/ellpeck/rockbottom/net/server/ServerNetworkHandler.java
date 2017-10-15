package de.ellpeck.rockbottom.net.server;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerNetworkHandler extends SimpleChannelInboundHandler<IPacket>{

    private final Server server;

    public ServerNetworkHandler(Server server){
        this.server = server;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        this.server.connectedChannels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        this.server.connectedChannels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket packet) throws Exception{
        packet.enqueueAsAction(RockBottomAPI.getGame(), ctx);
    }
}
