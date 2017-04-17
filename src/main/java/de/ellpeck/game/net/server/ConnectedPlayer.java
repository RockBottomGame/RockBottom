package de.ellpeck.game.net.server;

import de.ellpeck.game.Game;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.net.packet.toclient.PacketChunk;
import de.ellpeck.game.net.packet.toclient.PacketEntityChange;
import de.ellpeck.game.net.packet.toclient.PacketTileEntityData;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.tile.entity.TileEntity;
import io.netty.channel.Channel;
import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectedPlayer extends EntityPlayer{

    private final List<Chunk> sentLoadedChunks = new ArrayList<>();
    private Channel channel;

    public ConnectedPlayer(World world, UUID uniqueId){
        super(world, uniqueId);
    }

    @Override
    public void update(Game game){
        super.update(game);

        for(int i = 0; i < this.sentLoadedChunks.size(); i++){
            if(this.sentLoadedChunks.get(i).shouldUnload()){
                this.sentLoadedChunks.remove(i);
                i--;

                Log.info("Removing chunk from loaded list because it is unloaded");
            }
        }
    }

    @Override
    public void setChannel(Channel channel){
        this.channel = channel;
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
    public void onKeepLoaded(Chunk chunk){
        if(!this.sentLoadedChunks.contains(chunk)){
            this.sentLoadedChunks.add(chunk);

            Log.info("Sending chunk at "+chunk.gridX+", "+chunk.gridY+" to player with id "+this.getUniqueId());
            this.sendPacket(new PacketChunk(chunk));

            for(Entity entity : chunk.getAllEntities()){
                if(!entity.getUniqueId().equals(this.getUniqueId())){
                    this.sendPacket(new PacketEntityChange(entity, false));
                }
            }

            for(TileEntity tile : chunk.getAllTileEntities()){
                this.sendPacket(new PacketTileEntityData(tile.x, tile.y, tile));
            }
        }
    }
}
