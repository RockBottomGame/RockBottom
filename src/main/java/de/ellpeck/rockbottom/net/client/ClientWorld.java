package de.ellpeck.rockbottom.net.client;

import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.WorldTickEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldUnloadEvent;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.*;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClientWorld extends World {

    private SubWorldInitializer subWorld;

    public ClientWorld(WorldInfo info, DynamicRegistryInfo regInfo) {
        super(info, regInfo, null, true);
    }

    @Override
    public void unloadEverything() {
        this.loadedChunks.clear();
        this.chunkLookup.clear();

        if (this.hasAdditionalData()) {
            this.getAdditionalData().clear();
        }
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

            if (this.subWorld != null) {
                this.subWorld.update(this, game);
            }

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
    protected void saveChunk(IChunk chunk, boolean enqueue) {
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
    public World getSubWorld(ResourceName name) {
        throw new UnsupportedOperationException("Cannot get sub world in a client world");
    }

    @Override
    public List<? extends IWorld> getSubWorlds() {
        throw new UnsupportedOperationException("Cannot get sub world in a client world");
    }

    @Override
    public ResourceName getSubName() {
        if (this.subWorld == null) {
            return null;
        } else {
            return this.subWorld.getWorldName();
        }
    }

    @Override
    public String getName() {
        ResourceName sub = this.getSubName();
        return (sub == null ? "" : sub + "@") + "client_world";
    }

    @Override
    public Biome getExpectedBiome(int x, int y) {
        throw new UnsupportedOperationException("Cannot get world generation information in a client world");
    }

    @Override
    public BiomeLevel getExpectedBiomeLevel(int x, int y) {
        throw new UnsupportedOperationException("Cannot get world generation information in a client world");
    }

    @Override
    public int getExpectedSurfaceHeight(TileLayer layer, int x) {
        throw new UnsupportedOperationException("Cannot get world generation information in a client world");
    }

    @Override
    public IWorldGenerator getGenerator(ResourceName name) {
        throw new UnsupportedOperationException("Cannot get world generation information in a client world");
    }

    @Override
    public void callRetroactiveGeneration() {
        throw new UnsupportedOperationException("Cannot get world generation information in a client world");
    }

    @Override
    public List<IWorldGenerator> getSortedLoopingGenerators() {
        throw new UnsupportedOperationException("Cannot get world generation information in a client world");
    }

    @Override
    public List<IWorldGenerator> getSortedRetroactiveGenerators() {
        throw new UnsupportedOperationException("Cannot get world generation information in a client world");
    }

    @Override
    public Map<ResourceName, IWorldGenerator> getAllGenerators() {
        throw new UnsupportedOperationException("Cannot get world generation information in a client world");
    }

    @Override
    public void onUnloaded() {
        RockBottomAPI.getEventHandler().fireEvent(new WorldUnloadEvent(this));
    }

    @Override
    public void setSubName(ResourceName subName) {
        if (subName == null) {
            this.subWorld = null;
        } else {
            this.subWorld = Registries.SUB_WORLD_INITIALIZER_REGISTRY.get(subName);
        }
    }
}
