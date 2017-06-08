package de.ellpeck.rockbottom.game.net.client;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.util.MutableInt;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.game.world.Chunk;
import de.ellpeck.rockbottom.game.world.World;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;

import java.util.List;
import java.util.Map;

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
    public void update(IGameInstance game){
        this.checkListSync();

        if(!this.isGenerating){
            this.updateEntities(game);
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

    @Override
    public boolean shouldUnload(){
        return false;
    }

    @Override
    public List<EntityPlayer> getPlayersInRange(){
        throw new UnsupportedOperationException("Cannot get players in range of a client chunk");
    }

    @Override
    public List<EntityPlayer> getPlayersLeftRange(){
        throw new UnsupportedOperationException("Cannot get players that left range of a client chunk");
    }

    @Override
    public Map<EntityPlayer, MutableInt> getLeftPlayerTimers(){
        throw new UnsupportedOperationException("Cannot get timers for players that left range of a client chunk");
    }
}
