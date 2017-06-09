package de.ellpeck.rockbottom.api.net;

import de.ellpeck.rockbottom.api.data.settings.CommandPermissions;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;
import io.netty.channel.group.ChannelGroup;

public interface INetHandler{

    boolean isThePlayer(AbstractEntityPlayer player);

    boolean isClient();

    boolean isServer();

    boolean isActive();

    boolean isConnectedToServer();

    ChannelGroup getConnectedClients();

    CommandPermissions getCommandPermissions();

    void sendToServer(IPacket packet);

    void sendToAllPlayers(IWorld world, IPacket packet);

    void sendToAllPlayersExcept(IWorld world, IPacket packet, Entity except);

    void init(String ip, int port, boolean isServer) throws Exception;

    void shutdown();
}
