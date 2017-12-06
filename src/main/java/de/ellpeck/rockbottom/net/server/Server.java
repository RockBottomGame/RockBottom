package de.ellpeck.rockbottom.net.server;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.net.decode.PacketDecoder;
import de.ellpeck.rockbottom.net.encode.PacketEncoder;
import de.ellpeck.rockbottom.net.server.settings.Blacklist;
import de.ellpeck.rockbottom.net.server.settings.CommandPermissions;
import de.ellpeck.rockbottom.net.server.settings.Whitelist;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.compression.FastLzFrameDecoder;
import io.netty.handler.codec.compression.FastLzFrameEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;

public class Server{

    public final Channel channel;
    public final ChannelGroup connectedChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final EventLoopGroup group;

    public final CommandPermissions commandPermissions = new CommandPermissions();
    public final Whitelist whitelist = new Whitelist();
    public final Blacklist blacklist = new Blacklist();

    public Server(String ip, int port) throws Exception{
        IDataManager data = RockBottomAPI.getGame().getDataManager();
        data.loadPropSettings(this.commandPermissions);
        data.loadPropSettings(this.whitelist);
        data.loadPropSettings(this.blacklist);

        this.group = Epoll.isAvailable() ?
                new EpollEventLoopGroup(0, new DefaultThreadFactory("EpollServer", true)) :
                new NioEventLoopGroup(0, new DefaultThreadFactory("NioServer", true));

        this.channel = new ServerBootstrap()
                .group(this.group)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
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
                }).bind(ip != null ? InetAddress.getByName(ip) : null, port).syncUninterruptibly().channel();
    }

    public void shutdown(){
        this.group.shutdownGracefully();
    }
}
