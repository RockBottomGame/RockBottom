package de.ellpeck.game.net;

import de.ellpeck.game.net.client.Client;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.net.server.Server;
import de.ellpeck.game.util.Registry;
import io.netty.channel.epoll.Epoll;
import org.newdawn.slick.util.Log;

public final class NetHandler{

    public static final boolean HAS_EPOLL = Epoll.isAvailable();

    public static final Registry<Class<? extends IPacket>> PACKET_REGISTRY = new Registry<>("packet_registry", Byte.MAX_VALUE);

    static{

    }

    private static Client client;
    private static Server server;

    public static void init(boolean isServer){
        if(isActive()){
            Log.error("Cannot initialize "+(isServer ? "server" : "client")+" because one is already running: Client: "+client+", Server: "+server);
        }
        else{
            if(isServer){
                server = new Server(8000);
                Log.info("Started server!");
            }
            else{
                client = new Client("localhost", 8000);
                Log.info("Started client!");
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

    public static boolean isClient(){
        return client != null;
    }

    public static boolean isServer(){
        return server != null;
    }

    public static boolean isActive(){
        return isClient() || isServer();
    }

    public static void sendToServer(IPacket packet){
        client.channel.writeAndFlush(packet);
    }

    public static void sendToAllClients(IPacket packet){
        server.connectedChannels.writeAndFlush(packet);
    }
}
