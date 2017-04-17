package de.ellpeck.game.net.client;

import de.ellpeck.game.Constants;
import de.ellpeck.game.Game;
import de.ellpeck.game.util.Vec2;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.player.EntityPlayer;

import java.util.UUID;

public class ClientWorld extends World{

    public ClientWorld(WorldInfo info){
        super(info);
    }

    @Override
    protected Chunk loadChunk(int gridX, int gridY){
        Chunk chunk = new ClientChunk(this, gridX, gridY);

        this.loadedChunks.add(chunk);
        this.chunkLookup.put(new Vec2(gridX, gridY), chunk);

        return chunk;
    }

    @Override
    public void update(Game game){
        this.checkListSync();

        for(int x = -Constants.CHUNK_LOAD_DISTANCE; x <= Constants.CHUNK_LOAD_DISTANCE; x++){
            for(int y = -Constants.CHUNK_LOAD_DISTANCE; y <= Constants.CHUNK_LOAD_DISTANCE; y++){
                Chunk chunk = this.getChunkFromGridCoords(game.player.chunkX+x, game.player.chunkY+y);
                chunk.loadTimer = Constants.CHUNK_LOAD_TIME;
            }
        }

        for(int i = 0; i < this.loadedChunks.size(); i++){
            Chunk chunk = this.loadedChunks.get(i);
            chunk.update(game);

            chunk.loadTimer--;
            if(chunk.shouldUnload()){
                this.loadedChunks.remove(i);
                this.chunkLookup.remove(new Vec2(chunk.gridX, chunk.gridY));
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
    public EntityPlayer addPlayer(UUID id, boolean connected){
        if(connected){
            throw new UnsupportedOperationException("Cannot add a connected player to a client world");
        }

        EntityPlayer player = new EntityPlayer(this, id);
        this.addEntity(player);
        return player;
    }

    @Override
    public boolean isClient(){
        return true;
    }
}
