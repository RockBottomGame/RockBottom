package de.ellpeck.game.net;

import de.ellpeck.game.Game;
import de.ellpeck.game.net.client.Client;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.net.packet.toclient.*;
import de.ellpeck.game.net.packet.toserver.*;
import de.ellpeck.game.net.server.Server;
import de.ellpeck.game.util.Registry;
import de.ellpeck.game.util.Util;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.group.ChannelGroup;
import org.newdawn.slick.util.Log;

public final class NetHandler{

    public static final boolean HAS_EPOLL = Epoll.isAvailable();

    public static final Registry<Class<? extends IPacket>> PACKET_REGISTRY = new Registry<>("packet_registry", Byte.MAX_VALUE);

    static{
        PACKET_REGISTRY.register(0, PacketJoin.class);
        PACKET_REGISTRY.register(1, PacketChunk.class);
        PACKET_REGISTRY.register(2, PacketInitialServerData.class);
        PACKET_REGISTRY.register(3, PacketDisconnect.class);
        PACKET_REGISTRY.register(4, PacketTileChange.class);
        PACKET_REGISTRY.register(5, PacketMetaChange.class);
        PACKET_REGISTRY.register(6, PacketEntityChange.class);
        PACKET_REGISTRY.register(7, PacketBreakTile.class);
        PACKET_REGISTRY.register(8, PacketParticles.class);
        PACKET_REGISTRY.register(9, PacketEntityUpdate.class);
        PACKET_REGISTRY.register(10, PacketPlayerMovement.class);
        PACKET_REGISTRY.register(11, PacketInteract.class);
        PACKET_REGISTRY.register(12, PacketHotbar.class);
        PACKET_REGISTRY.register(13, PacketTileEntityData.class);
        PACKET_REGISTRY.register(14, PacketSlotModification.class);
        PACKET_REGISTRY.register(15, PacketOpenUnboundContainer.class);
        PACKET_REGISTRY.register(16, PacketContainerData.class);
        PACKET_REGISTRY.register(17, PacketContainerChange.class);
    }

    private static Client client;
    private static Server server;

    public static void init(String ip, int port, boolean isServer) throws Exception{
        if(isActive()){
            Log.error("Cannot initialize "+(isServer ? "server" : "client")+" because one is already running: Client: "+client+", Server: "+server);
        }
        else{
            if(isServer){
                server = new Server(ip, port);
                Log.info("Started server with ip "+ip+" on port "+port);
            }
            else{
                client = new Client(ip, port);
                Log.info("Started client with ip "+ip+" on port "+port);
            }
        }
    }

    public static void shutdown(){
        if(isClient()){
            client.shutdown();
            client = null;

            Log.info("Shut down client!");
        }

        if(isServer()){
            server.shutdown();
            server = null;

            Log.info("Shut down server!");
        }
    }

    public static boolean isThePlayer(EntityPlayer player){
        return Game.get().player == player;
    }

    public static boolean isClient(){
        return client != null;
    }

    public static boolean isServer(){
        return server != null;
    }

    public static boolean isActive(){
        return isClient() || isServer();
    }

    public static ChannelGroup getConnectedClients(){
        if(isServer()){
            return server.connectedChannels;
        }
        return null;
    }

    public static void sendToServer(IPacket packet){
        client.channel.writeAndFlush(packet);
    }

    public static void sendToAllClients(IPacket packet){
        server.connectedChannels.writeAndFlush(packet);
    }

    public static void sendToPlayersInArea(World world, IPacket packet, double x, double y, double radius){
        for(EntityPlayer player : world.players){
            if(Util.distanceSq(x, y, player.x, player.y) <= radius*radius){
                player.sendPacket(packet);
            }
        }
    }

    public static void sendToAllPlayers(World world, IPacket packet){
        sendToAllPlayersExcept(world, packet, null);
    }

    public static void sendToAllPlayersExcept(World world, IPacket packet, Entity except){
        for(EntityPlayer player : world.players){
            if(player != except){
                player.sendPacket(packet);
            }
        }
    }
}
