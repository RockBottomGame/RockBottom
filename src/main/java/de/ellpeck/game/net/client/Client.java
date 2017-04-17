package de.ellpeck.game.net.client;

import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.decode.PacketDecoder;
import de.ellpeck.game.net.encode.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.FastLzFrameDecoder;
import io.netty.handler.codec.compression.FastLzFrameEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;

public class Client{

    private final EventLoopGroup group;
    public final Channel channel;

    public Client(String ip, int port) throws Exception{
        this.group = NetHandler.HAS_EPOLL ?
                new EpollEventLoopGroup(0, new DefaultThreadFactory("EpollClient", true)) :
                new NioEventLoopGroup(0, new DefaultThreadFactory("NioClient", true));

        this.channel = new Bootstrap()
                .group(this.group)
                .channel(NetHandler.HAS_EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(new ChannelInitializer(){
                    @Override
                    protected void initChannel(Channel channel) throws Exception{
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);

                        channel.pipeline()
                                .addLast(new FastLzFrameDecoder())
                                .addLast(new PacketDecoder())
                                .addLast(new FastLzFrameEncoder())
                                .addLast(new PacketEncoder())
                                .addLast(new ClientNetworkHandler());
                    }
                }).connect(ip, port).syncUninterruptibly().channel();
    }

    public void shutdown(){
        this.group.shutdownGracefully();
    }
}
