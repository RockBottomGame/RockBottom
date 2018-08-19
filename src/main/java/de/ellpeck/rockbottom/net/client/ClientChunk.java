package de.ellpeck.rockbottom.net.client;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.util.Counter;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.World;

import java.util.List;
import java.util.Map;

public class ClientChunk extends Chunk {

    public ClientChunk(World world, int gridX, int gridY, boolean isPersistent) {
        super(world, gridX, gridY, isPersistent);
    }

    @Override
    public void loadOrCreate(DataSet set) {
        throw new UnsupportedOperationException("Cannot load or create client chunk");
    }

    @Override
    public void callRetroactiveGeneration() {
        throw new UnsupportedOperationException("Cannot generate client chunk");
    }

    @Override
    public void save(DataSet set) {
        throw new UnsupportedOperationException("Cannot save client chunk");
    }

    @Override
    public void update(IGameInstance game) {
        if (!this.isGenerating) {
            this.updateEntities(game);
        }
    }

    @Override
    public void scheduleUpdate(int x, int y, TileLayer layer, int scheduledMeta, int time) {
        throw new UnsupportedOperationException("Cannot schedule updates in a client chunk");
    }

    @Override
    public int getScheduledUpdateAmount() {
        return 0;
    }

    @Override
    public boolean shouldUnload() {
        return false;
    }

    @Override
    public List<AbstractEntityPlayer> getPlayersInRange() {
        throw new UnsupportedOperationException("Cannot get players in range of a client chunk");
    }

    @Override
    public List<AbstractEntityPlayer> getPlayersLeftRange() {
        throw new UnsupportedOperationException("Cannot get players that left range of a client chunk");
    }

    @Override
    public Map<AbstractEntityPlayer, Counter> getLeftPlayerTimers() {
        throw new UnsupportedOperationException("Cannot get timers for players that left range of a client chunk");
    }
}
