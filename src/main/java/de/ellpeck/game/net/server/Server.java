package de.ellpeck.game.net.server;

import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.decode.PacketDecoder;
import de.ellpeck.game.net.encode.PacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.compression.FastLzFrameDecoder;
import io.netty.handler.codec.compression.FastLzFrameEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server{

    private final EventLoopGroup group;
    public final Channel channel;

    public final ChannelGroup connectedChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public Server(String ip, int port) throws Exception{
        this.group = NetHandler.HAS_EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        this.channel = new ServerBootstrap()
                .group(this.group)
                .channel(NetHandler.HAS_EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer(){
                    @Override
                    protected void initChannel(Channel channel) throws Exception{
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);

                        channel.pipeline()
                                .addLast(new FastLzFrameDecoder())
                                .addLast(new PacketDecoder())
                                .addLast(new FastLzFrameEncoder())
                                .addLast(new PacketEncoder())
                                .addLast(new ServerNetworkHandler(Server.this));
                    }
                }).bind(ip, port).syncUninterruptibly().channel();
    }

    public void shutdown(){
        this.group.shutdownGracefully();
    }
}
