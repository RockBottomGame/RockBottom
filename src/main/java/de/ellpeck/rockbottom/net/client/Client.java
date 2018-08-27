package de.ellpeck.rockbottom.net.client;

import de.ellpeck.rockbottom.net.PacketDecoder;
import de.ellpeck.rockbottom.net.PacketEncoder;
import de.ellpeck.rockbottom.net.TrafficMonitor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.FastLzFrameDecoder;
import io.netty.handler.codec.compression.FastLzFrameEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;

public class Client {

    public final Channel channel;
    private final EventLoopGroup group;

    public Client(String ip, int port) {
        this.group = Epoll.isAvailable() ?
                new EpollEventLoopGroup(0, new DefaultThreadFactory("EpollClient", true)) :
                new NioEventLoopGroup(0, new DefaultThreadFactory("NioClient", true));

        this.channel = new Bootstrap()
                .group(this.group)
                .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);

                        channel.pipeline()
                                .addLast(new FastLzFrameDecoder())
                                .addLast(new PacketDecoder())
                                .addLast(new FastLzFrameEncoder())
                                .addLast(new PacketEncoder())
                                .addLast(new ClientNetworkHandler())
                                .addLast(new TrafficMonitor());
                    }
                }).connect(ip, port).syncUninterruptibly().channel();
    }

    public void shutdown() {
        this.group.shutdownGracefully();
    }
}
