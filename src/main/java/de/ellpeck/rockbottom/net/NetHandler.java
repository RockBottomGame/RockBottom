package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.data.settings.CommandPermissions;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.INetHandler;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.net.client.Client;
import de.ellpeck.rockbottom.net.server.Server;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.channel.group.ChannelGroup;
import org.newdawn.slick.util.Log;

public final class NetHandler implements INetHandler{

    private Client client;
    private Server server;

    @Override
    public void init(String ip, int port, boolean isServer) throws Exception{
        if(this.isActive()){
            Log.error("Cannot initialize "+(isServer ? "server" : "client")+" because one is already running: Client: "+this.client+", Server: "+this.server);
        }
        else{
            if(isServer){
                this.server = new Server(ip, port);
                Log.info("Started server with ip "+ip+" on port "+port);
            }
            else{
                this.client = new Client(ip, port);
                Log.info("Started client with ip "+ip+" on port "+port);
            }
        }
    }

    @Override
    public void shutdown(){
        if(this.isClient()){
            this.client.shutdown();
            this.client = null;

            Log.info("Shut down client!");
        }

        if(this.isServer()){
            this.server.shutdown();
            this.server = null;

            Log.info("Shut down server!");
        }
    }

    @Override
    public boolean isThePlayer(AbstractEntityPlayer player){
        return RockBottom.get().getPlayer() == player;
    }

    @Override
    public boolean isClient(){
        return this.client != null;
    }

    @Override
    public boolean isServer(){
        return this.server != null;
    }

    @Override
    public boolean isActive(){
        return this.isClient() || this.isServer();
    }

    @Override
    public boolean isConnectedToServer(){
        return this.isClient() && this.client.channel.isOpen();
    }

    @Override
    public ChannelGroup getConnectedClients(){
        if(this.isServer()){
            return this.server.connectedChannels;
        }
        else{
            return null;
        }
    }

    @Override
    public CommandPermissions getCommandPermissions(){
        if(this.isServer()){
            return this.server.commandPermissions;
        }
        else{
            return null;
        }
    }

    @Override
    public void sendToServer(IPacket packet){
        if(this.isClient()){
            this.client.channel.writeAndFlush(packet);
        }
    }

    @Override
    public void sendToAllPlayers(IWorld world, IPacket packet){
        this.sendToAllPlayersExcept(world, packet, null);
    }

    @Override
    public void sendToAllPlayersExcept(IWorld world, IPacket packet, Entity except){
        if(this.isServer()){
            if(world instanceof World){
                for(EntityPlayer player : ((World)world).players){
                    if(player != except){
                        player.sendPacket(packet);
                    }
                }
            }
        }
    }
}
