package de.ellpeck.rockbottom.net.server;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.net.packet.toclient.PacketTileEntityData;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.channel.Channel;
import org.newdawn.slick.util.Log;

import java.util.UUID;

public class ConnectedPlayer extends EntityPlayer{

    private final Channel channel;

    private int lastHealth;

    public ConnectedPlayer(World world, UUID uniqueId, Channel channel){
        super(world, uniqueId);
        this.channel = channel;
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        if(this.ticksExisted%80 == 0){
            if(!RockBottomAPI.getNet().getConnectedClients().contains(this.channel)){
                game.scheduleAction(() -> {
                    game.getWorld().savePlayer(this);
                    game.getWorld().removeEntity(this);

                    Log.info("Saving and removing disconnected player with id "+this.getUniqueId()+" from world");

                    return true;
                });
            }
        }


        if(this.health != this.lastHealth && this.world.getWorldInfo().totalTimeInWorld%10 == 0){
            this.lastHealth = this.health;

            if(RockBottomAPI.getNet().isServer()){
                this.sendPacket(new PacketHealth(this.health));
            }
        }
    }

    @Override
    public void resetAndSpawn(IGameInstance game){
        super.resetAndSpawn(game);

        this.sendPacket(new PacketRespawn());
    }

    @Override
    public int getUpdateFrequency(){
        return 80;
    }

    @Override
    public void sendPacket(IPacket packet){
        if(this.channel != null){
            this.channel.writeAndFlush(packet);
        }
    }

    @Override
    public void onChunkLoaded(IChunk chunk){
        Log.debug("Sending chunk at "+chunk.getGridX()+", "+chunk.getGridY()+" to player with id "+this.getUniqueId());

        this.sendPacket(new PacketChunk(chunk));

        for(Entity entity : chunk.getAllEntities()){
            if(entity != this){
                this.sendPacket(new PacketEntityChange(entity, false));
            }
        }

        for(TileEntity tile : chunk.getAllTileEntities()){
            this.sendPacket(new PacketTileEntityData(tile.x, tile.y, tile));
        }
    }

    @Override
    public void onChunkUnloaded(IChunk chunk){
        Log.debug("Sending chunk unloading packet for chunk at "+chunk.getGridX()+", "+chunk.getGridY()+" to player with id "+this.getUniqueId());

        this.sendPacket(new PacketChunkUnload(chunk.getGridX(), chunk.getGridY()));
    }
}
