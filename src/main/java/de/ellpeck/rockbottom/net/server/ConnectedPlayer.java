package de.ellpeck.rockbottom.net.server;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.IPacket;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.Entity;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntity;
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
    public void update(RockBottom game){
        super.update(game);

        if(this.ticksExisted%80 == 0){
            if(!NetHandler.getConnectedClients().contains(this.channel)){
                game.scheduleAction(() -> {
                    game.world.savePlayer(this);
                    game.world.removeEntity(this);

                    Log.info("Saving and removing disconnected player with id "+this.getUniqueId()+" from world");

                    return true;
                });
            }
        }


        if(this.health != this.lastHealth && this.world.info.totalTimeInWorld%10 == 0){
            this.lastHealth = this.health;

            if(NetHandler.isServer()){
                this.sendPacket(new PacketHealth(this.health));
            }
        }
    }

    @Override
    public void resetAndSpawn(RockBottom game){
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
    public void onChunkLoaded(Chunk chunk){
        Log.debug("Sending chunk at "+chunk.gridX+", "+chunk.gridY+" to player with id "+this.getUniqueId());

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
    public void onChunkUnloaded(Chunk chunk){
        Log.debug("Sending chunk unloading packet for chunk at "+chunk.gridX+", "+chunk.gridY+" to player with id "+this.getUniqueId());

        this.sendPacket(new PacketChunkUnload(chunk.gridX, chunk.gridY));
    }
}
