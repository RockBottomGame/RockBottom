package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.data.settings.CommandPermissions;
import de.ellpeck.rockbottom.net.client.Client;
import de.ellpeck.rockbottom.net.packet.IPacket;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.net.packet.toserver.*;
import de.ellpeck.rockbottom.net.server.Server;
import de.ellpeck.rockbottom.util.Registry;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.Entity;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.group.ChannelGroup;
import org.newdawn.slick.util.Log;

public final class NetHandler{

    public static final boolean HAS_EPOLL = Epoll.isAvailable();

    public static final Registry<Class<? extends IPacket>> PACKET_REGISTRY = new Registry<>("packet_registry", Byte.MAX_VALUE);
    private static Client client;
    private static Server server;

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
        PACKET_REGISTRY.register(18, PacketChatMessage.class);
        PACKET_REGISTRY.register(19, PacketSendChat.class);
        PACKET_REGISTRY.register(20, PacketHealth.class);
        PACKET_REGISTRY.register(21, PacketRespawn.class);
        PACKET_REGISTRY.register(22, PacketDropItem.class);
        PACKET_REGISTRY.register(23, PacketChunkUnload.class);
        PACKET_REGISTRY.register(24, PacketManualConstruction.class);
    }

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
        return RockBottom.get().player == player;
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

    public static boolean isConnectedToServer(){
        return isClient() && client.channel.isOpen();
    }

    public static ChannelGroup getConnectedClients(){
        if(isServer()){
            return server.connectedChannels;
        }
        else{
            return null;
        }
    }

    public static CommandPermissions getCommandPermissions(){
        if(isServer()){
            return server.commandPermissions;
        }
        else{
            return null;
        }
    }

    public static void sendToServer(IPacket packet){
        client.channel.writeAndFlush(packet);
    }

    public static void sendToAllPlayers(World world, IPacket packet){
        sendToAllPlayersExcept(world, packet, null);
    }

    public static void sendToAllPlayersExcept(World world, IPacket packet, Entity except){
        if(NetHandler.isServer()){
            for(EntityPlayer player : world.players){
                if(player != except){
                    player.sendPacket(packet);
                }
            }
        }
    }
}
