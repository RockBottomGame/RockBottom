package de.ellpeck.rockbottom.world;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import io.netty.channel.Channel;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SubWorld extends AbstractWorld {

    private final AbstractWorld mainWorld;
    private final int seedModifier;

    public SubWorld(String worldName, AbstractWorld mainWorld, int seedModifier) {
        super(new File(mainWorld.directory, worldName));
        this.mainWorld = mainWorld;
        this.seedModifier = seedModifier;
    }

    @Override
    protected List<Pos2> getDefaultPersistentChunks() {
        return Collections.emptyList();
    }

    @Override
    public int getIdForState(TileState state) {
        return this.mainWorld.getIdForState(state);
    }

    @Override
    public TileState getStateForId(int id) {
        return this.mainWorld.getStateForId(id);
    }

    @Override
    public NameToIndexInfo getTileRegInfo() {
        return this.mainWorld.getTileRegInfo();
    }

    @Override
    public int getIdForBiome(Biome biome) {
        return this.mainWorld.getIdForBiome(biome);
    }

    @Override
    public Biome getBiomeForId(int id) {
        return this.mainWorld.getBiomeForId(id);
    }

    @Override
    public NameToIndexInfo getBiomeRegInfo() {
        return this.mainWorld.getBiomeRegInfo();
    }

    @Override
    public DynamicRegistryInfo getRegInfo() {
        return this.mainWorld.getRegInfo();
    }

    @Override
    public WorldInfo getWorldInfo() {
        return this.mainWorld.getWorldInfo();
    }

    @Override
    public AbstractEntityPlayer createPlayer(UUID id, IPlayerDesign design, Channel channel, boolean loadOrSwapLast) {
        throw new UnsupportedOperationException("Cannot create a player in a sub world");
    }

    @Override
    public AbstractEntityPlayer getPlayer(UUID id) {
        return this.mainWorld.getPlayer(id);
    }

    @Override
    public AbstractEntityPlayer getPlayer(String name) {
        return this.mainWorld.getPlayer(name);
    }

    @Override
    public int getSpawnX() {
        return 0;
    }

    @Override
    public IWorldGenerator getGenerator(ResourceName name) {
        return this.mainWorld.getGenerator(name);
    }

    @Override
    public void savePlayer(AbstractEntityPlayer player) {
        this.mainWorld.savePlayer(player);
    }

    @Override
    public Map<ResourceName, IWorldGenerator> getAllGenerators() {
        return this.mainWorld.getAllGenerators();
    }

    @Override
    public List<IWorldGenerator> getSortedLoopingGenerators() {
        return this.mainWorld.getSortedLoopingGenerators();
    }

    @Override
    public List<IWorldGenerator> getSortedRetroactiveGenerators() {
        return this.mainWorld.getSortedRetroactiveGenerators();
    }

    @Override
    public List<AbstractEntityPlayer> getAllPlayers() {
        return this.mainWorld.getAllPlayers();
    }

    @Override
    public File getPlayerFolder() {
        return this.mainWorld.getPlayerFolder();
    }

    @Override
    public boolean isStoryMode() {
        return this.mainWorld.isStoryMode();
    }

    @Override
    public AbstractEntityPlayer getClosestPlayer(double x, double y, AbstractEntityPlayer excluding) {
        return this.mainWorld.getClosestPlayer(x, y, excluding);
    }

    @Override
    public AbstractEntityPlayer getClosestPlayer(double x, double y) {
        return this.mainWorld.getClosestPlayer(x, y);
    }

    @Override
    public void addPlayer(AbstractEntityPlayer player) {
        this.mainWorld.addPlayer(player);
    }

    @Override
    public void removePlayer(AbstractEntityPlayer player) {
        this.mainWorld.removePlayer(player);
    }

    @Override
    public long getSeed() {
        return Util.scrambleSeed(this.seedModifier, this.mainWorld.getSeed());
    }
}
