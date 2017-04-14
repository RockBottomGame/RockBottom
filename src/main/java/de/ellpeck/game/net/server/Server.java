package de.ellpeck.game.net.server;

import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.PacketDecoder;
import de.ellpeck.game.net.PacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.newdawn.slick.util.Log;

public class Server{

    private final EventLoopGroup group;
    public Channel channel;

    public ChannelGroup connectedChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public Server(){
        this.group = NetHandler.HAS_EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        try{
            this.channel = new ServerBootstrap()
                    .group(this.group)
                    .channel(NetHandler.HAS_EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer(){
                        @Override
                        protected void initChannel(Channel channel) throws Exception{
                            channel.pipeline()
                                    .addLast(new PacketDecoder())
                                    .addLast(new PacketEncoder())
                                    .addLast(new ServerNetworkHandler(Server.this));
                        }
                    }).bind(8000).sync().channel();
        }
        catch(InterruptedException e){
            Log.error(e);
        }
    }

    public void shutdown(){
        this.group.shutdownGracefully();
    }
}
