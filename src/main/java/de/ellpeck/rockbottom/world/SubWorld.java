package de.ellpeck.rockbottom.world;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.SubWorldInitializer;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.api.world.gen.BiomeGen;
import de.ellpeck.rockbottom.api.world.gen.HeightGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.init.AbstractGame;
import io.netty.channel.Channel;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class SubWorld extends AbstractWorld {

    private final AbstractWorld mainWorld;
    private final SubWorldInitializer initializer;

    public SubWorld(AbstractWorld mainWorld, ResourceName name, SubWorldInitializer initializer) {
        super(new File(mainWorld.getFolder(), name.toString()), Util.scrambleSeed(initializer.getSeedModifier(), mainWorld.getSeed()));
        this.mainWorld = mainWorld;
        this.initializer = initializer;
    }

    @Override
    public boolean update(AbstractGame game) {
        if (super.update(game)) {
            this.initializer.update(this, game);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected List<Pos2> getDefaultPersistentChunks() {
        return this.initializer.getDefaultPersistentChunks(this);
    }

    @Override
    protected boolean shouldGenerateHere(IWorldGenerator generator, ResourceName name) {
        return this.initializer.shouldGenerateHere(this, name, generator);
    }

    @Override
    protected BiomeGen getBiomeGen() {
        return this.initializer.initBiomeGen(this);
    }

    @Override
    protected HeightGen getHeightGen() {
        return this.initializer.initHeightGen(this);
    }

    @Override
    public boolean renderSky(IGameInstance game, IAssetManager manager, IRenderer g, AbstractWorld world, AbstractEntityPlayer player, double width, double height) {
        return this.initializer.renderSky(this, game, manager, g, world, player, width, height);
    }

    @Override
    protected void saveImpl(long startTime) {
        this.initializer.onSave(this);
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
        return this.mainWorld.createPlayer(id, design, channel, loadOrSwapLast);
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
    public void savePlayer(AbstractEntityPlayer player) {
        this.mainWorld.savePlayer(player);
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
    public void travelToSubWorld(Entity entity, ResourceName subWorld, double x, double y) {
        this.mainWorld.travelToSubWorld(entity, subWorld, x, y);
    }

    @Override
    public IWorld getSubWorld(ResourceName name) {
        return this.mainWorld.getSubWorld(name);
    }

    @Override
    public List<? extends IWorld> getSubWorlds() {
        return this.mainWorld.getSubWorlds();
    }

    @Override
    public IWorld getMainWorld() {
        return this.mainWorld;
    }

    @Override
    public ResourceName getSubName() {
        return this.initializer.getWorldName();
    }

    @Override
    public String getName() {
        return this.getSubName() + "@" + this.mainWorld.getName();
    }
}
