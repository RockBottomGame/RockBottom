package de.ellpeck.game.net.client;

import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.util.Util;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.tile.entity.TileEntity;

public class ClientChunk extends Chunk{

    public ClientChunk(World world, int gridX, int gridY){
        super(world, gridX, gridY);
    }

    @Override
    public void loadOrCreate(DataSet set){
        throw new UnsupportedOperationException("Cannot load or create client chunk");
    }

    @Override
    public void save(DataSet set){
        throw new UnsupportedOperationException("Cannot save client chunk");
    }

    @Override
    public void update(Game game){
        this.checkListSync();

        if(!this.isGenerating){
            for(int i = 0; i < this.entities.size(); i++){
                Entity entity = this.entities.get(i);
                entity.update(game);

                if(entity.shouldBeRemoved()){
                    this.world.removeEntity(entity);
                    i--;
                }
                else{
                    int newChunkX = Util.toGridPos(entity.x);
                    int newChunkY = Util.toGridPos(entity.y);

                    if(newChunkX != this.gridX || newChunkY != this.gridY){
                        this.removeEntity(entity);
                        i--;

                        Chunk chunk = this.world.getChunkFromGridCoords(newChunkX, newChunkY);
                        chunk.addEntity(entity);
                    }
                }
            }

            for(int i = 0; i < this.tileEntities.size(); i++){
                TileEntity tile = this.tileEntities.get(i);
                tile.update(game);

                if(tile.shouldRemove()){
                    this.removeTileEntity(tile.x, tile.y);
                    i--;
                }
            }
        }
    }

    @Override
    public void scheduleUpdate(int x, int y, TileLayer layer, int time){
        throw new UnsupportedOperationException("Cannot schedule updates in a client chunk");
    }

    @Override
    public int getScheduledUpdateAmount(){
        return 0;
    }
}
