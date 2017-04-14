package de.ellpeck.game.net.client;

import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.PacketDecoder;
import de.ellpeck.game.net.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client{

    private final EventLoopGroup group;
    public final Channel channel;

    public Client(String ip, int port){
        this.group = NetHandler.HAS_EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        this.channel = new Bootstrap()
                .group(this.group)
                .channel(NetHandler.HAS_EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(new ChannelInitializer(){
                    @Override
                    protected void initChannel(Channel channel) throws Exception{
                        channel.pipeline()
                                .addLast(new PacketDecoder())
                                .addLast(new PacketEncoder())
                                .addLast(new ClientNetworkHandler());
                    }
                }).connect(ip, port).syncUninterruptibly().channel();
    }

    public void shutdown(){
        this.group.shutdownGracefully();
    }
}
