package de.ellpeck.rockbottom.net.client;

import de.ellpeck.rockbottom.Constants;
import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.util.Pos2;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.Entity;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.channel.Channel;

import java.util.UUID;

public class ClientWorld extends World{

    public ClientWorld(WorldInfo info){
        super(info);
    }

    @Override
    protected Chunk loadChunk(int gridX, int gridY){
        Chunk chunk = new ClientChunk(this, gridX, gridY);

        this.loadedChunks.add(chunk);
        this.chunkLookup.put(new Pos2(gridX, gridY), chunk);

        return chunk;
    }

    @Override
    public void unloadChunk(Chunk chunk){
        this.loadedChunks.remove(chunk);
        this.chunkLookup.remove(new Pos2(chunk.gridX, chunk.gridY));
    }

    @Override
    public void update(RockBottom game){
        this.checkListSync();

        for(int i = 0; i < this.loadedChunks.size(); i++){
            Chunk chunk = this.loadedChunks.get(i);
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
    protected void saveChunk(Chunk chunk){
        throw new UnsupportedOperationException("Cannot save chunk in client world");
    }

    @Override
    public void savePlayer(EntityPlayer player){
        throw new UnsupportedOperationException("Cannot save player in client world");
    }

    @Override
    public EntityPlayer getPlayer(UUID id){
        throw new UnsupportedOperationException("Cannot get player in client world");
    }

    @Override
    public EntityPlayer createPlayer(UUID id, Channel channel){
        if(channel != null){
            throw new UnsupportedOperationException("Cannot create a connected player in a client world");
        }
        return new EntityPlayer(this, id);
    }

    @Override
    public boolean isClient(){
        return true;
    }

    @Override
    public void addEntity(Entity entity){
        Chunk chunk = this.getChunk(entity.x, entity.y);
        chunk.addEntity(entity);
    }

    @Override
    public void removeEntity(Entity entity){
        Chunk chunk = this.getChunk(entity.x, entity.y);
        chunk.removeEntity(entity);

        entity.onRemoveFromWorld();
    }
}
