package de.ellpeck.rockbottom.net.client;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.channel.Channel;

import java.util.UUID;

public class ClientWorld extends World{

    public ClientWorld(WorldInfo info, NameToIndexInfo tileRegInfo, NameToIndexInfo biomeRegInfo){
        super(info, tileRegInfo, biomeRegInfo);
    }

    @Override
    protected Chunk loadChunk(int gridX, int gridY){
        Chunk chunk = new ClientChunk(this, gridX, gridY);

        this.loadedChunks.add(chunk);
        this.chunkLookup.put(new Pos2(gridX, gridY), chunk);

        return chunk;
    }

    @Override
    public void unloadChunk(IChunk chunk){
        this.loadedChunks.remove(chunk);
        this.chunkLookup.remove(new Pos2(chunk.getGridX(), chunk.getGridY()));
    }

    @Override
    public void update(RockBottom game){
        this.checkListSync();

        for(int i = 0; i < this.loadedChunks.size(); i++){
            IChunk chunk = this.loadedChunks.get(i);
            chunk.update(game);

            if(chunk.shouldUnload()){
                this.unloadChunk(chunk);
                i--;
            }
        }

        this.info.totalTimeInWorld++;

        this.info.currentWorldTime++;
        if(this.info.currentWorldTime >= Constants.TIME_PER_DAY){
            this.info.currentWorldTime = 0;
        }
    }

    @Override
    public void save(){
        throw new UnsupportedOperationException("Cannot save client world");
    }

    @Override
    protected void saveChunk(IChunk chunk){
        throw new UnsupportedOperationException("Cannot save chunk in client world");
    }

    @Override
    public EntityPlayer getPlayer(UUID id){
        throw new UnsupportedOperationException("Cannot get player in client world");
    }

    @Override
    public AbstractEntityPlayer getPlayer(String name){
        throw new UnsupportedOperationException("Cannot get player in client world");
    }

    @Override
    public EntityPlayer createPlayer(UUID id, String name, Channel channel){
        if(channel != null){
            throw new UnsupportedOperationException("Cannot create a connected player in a client world");
        }
        return new EntityPlayer(this, id, name);
    }

    @Override
    public void addEntity(Entity entity){
        IChunk chunk = this.getChunk(entity.x, entity.y);
        chunk.addEntity(entity);
    }

    @Override
    public void removeEntity(Entity entity){
        IChunk chunk = this.getChunk(entity.x, entity.y);
        chunk.removeEntity(entity);

        entity.onRemoveFromWorld();
    }
}
