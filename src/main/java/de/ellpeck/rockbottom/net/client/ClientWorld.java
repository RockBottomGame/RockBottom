package de.ellpeck.rockbottom.net.client;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.WorldTickEvent;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.channel.Channel;

import java.util.List;
import java.util.UUID;

public class ClientWorld extends World {

    public ClientWorld(WorldInfo info, DynamicRegistryInfo regInfo) {
        super(info, regInfo, null);
    }

    @Override
    protected Chunk loadChunk(int gridX, int gridY, boolean isPersistent, boolean enqueue) {
        Chunk chunk = new ClientChunk(this, gridX, gridY, isPersistent);

        this.loadedChunks.add(chunk);
        this.chunkLookup.put(gridX, gridY, chunk);

        return chunk;
    }

    @Override
    public void unloadChunk(IChunk chunk) {
        this.loadedChunks.remove(chunk);
        this.chunkLookup.remove(chunk.getGridX(), chunk.getGridY());
    }

    @Override
    public boolean update(AbstractGame game) {
        if (RockBottomAPI.getEventHandler().fireEvent(new WorldTickEvent(this)) != EventResult.CANCELLED) {
            this.updateChunksAndTime(game);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Cannot save client world");
    }

    @Override
    protected boolean saveChunk(IChunk chunk, boolean enqueue) {
        throw new UnsupportedOperationException("Cannot save chunk in client world");
    }

    @Override
    public EntityPlayer createPlayer(UUID id, IPlayerDesign design, Channel channel, boolean loadOrSwapLast) {
        if (channel != null) {
            throw new UnsupportedOperationException("Cannot create a connected player in a client world");
        } else {
            return new EntityPlayer(this, id, design);
        }
    }

    @Override
    public IWorld getSubWorld(ResourceName name) {
        throw new UnsupportedOperationException("Cannot get sub worlds in a client world");
    }

    @Override
    public List<IWorld> getSubWorlds() {
        throw new UnsupportedOperationException("Cannot get sub worlds in a client world");
    }

    @Override
    public String getName() {
        return "Client world";
    }
}
