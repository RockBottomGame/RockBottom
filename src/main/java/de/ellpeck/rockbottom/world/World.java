package de.ellpeck.rockbottom.world;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.impl.PlayerJoinWorldEvent;
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
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.net.packet.toclient.PacketPlayer;
import de.ellpeck.rockbottom.net.server.ConnectedPlayer;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.channel.Channel;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class World extends AbstractWorld {

    protected final List<AbstractEntityPlayer> players = new ArrayList<>();
    protected final List<AbstractEntityPlayer> playersUnmodifiable = Collections.unmodifiableList(this.players);
    protected final DynamicRegistryInfo regInfo;
    protected final WorldInfo info;
    protected Map<ResourceName, IWorldGenerator> generators;
    protected List<IWorldGenerator> loopingGenerators;
    protected List<IWorldGenerator> retroactiveGenerators;
    protected int saveTicksCounter;
    protected File playerDirectory;

    public World(WorldInfo info, DynamicRegistryInfo regInfo, File worldDirectory) {
        super(worldDirectory);
        this.info = info;
        this.regInfo = regInfo;

        this.playerDirectory = new File(worldDirectory, "players");

        this.initGenerators();
    }

    private void initGenerators() {
        Map<ResourceName, IWorldGenerator> generators = new HashMap<>();
        List<IWorldGenerator> loopingGenerators = new ArrayList<>();
        List<IWorldGenerator> retroactiveGenerators = new ArrayList<>();

        for (Map.Entry<ResourceName, Class<? extends IWorldGenerator>> entry : Registries.WORLD_GENERATORS.entrySet()) {
            try {
                IWorldGenerator generator = entry.getValue().getConstructor().newInstance();
                generator.initWorld(this);

                if (generator.generatesPerChunk()) {
                    loopingGenerators.add(generator);

                    if (generator.generatesRetroactively()) {
                        retroactiveGenerators.add(generator);
                    }
                }

                generators.put(entry.getKey(), generator);
            } catch (Exception e) {
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't initialize world generator with class " + entry.getValue(), e);
            }
        }

        Comparator comp = Comparator.comparingInt(IWorldGenerator::getPriority).reversed();
        loopingGenerators.sort(comp);
        retroactiveGenerators.sort(comp);

        this.generators = Collections.unmodifiableMap(generators);
        this.loopingGenerators = Collections.unmodifiableList(loopingGenerators);
        this.retroactiveGenerators = Collections.unmodifiableList(retroactiveGenerators);

        RockBottomAPI.logger().info("Added a total of " + this.generators.size() + " generators to world (" + this.loopingGenerators.size() + " per chunk, " + this.retroactiveGenerators.size() + " retroactive)");

        this.onGeneratorsLoaded();
    }

    @Override
    protected List<Pos2> getDefaultPersistentChunks() {
        List<Pos2> chunks = new ArrayList<>();
        for (int x = -Constants.PERSISTENT_CHUNK_DISTANCE; x <= Constants.PERSISTENT_CHUNK_DISTANCE; x++) {
            for (int y = Constants.PERSISTENT_CHUNK_DISTANCE; y >= -Constants.PERSISTENT_CHUNK_DISTANCE; y--) {
                chunks.add(new Pos2(x, y));
            }
        }
        return chunks;
    }

    @Override
    public boolean update(AbstractGame game) {
        if (super.update(game)) {
            this.saveTicksCounter++;
            if (this.saveTicksCounter >= game.getAutosaveInterval() * Constants.TARGET_TPS) {
                this.saveTicksCounter = 0;

                this.save();
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getIdForState(TileState state) {
        ResourceName name = Registries.TILE_STATE_REGISTRY.getId(state);
        if (name != null) {
            return this.getTileRegInfo().getId(name);
        } else {
            return -1;
        }
    }

    @Override
    public TileState getStateForId(int id) {
        ResourceName name = this.getTileRegInfo().get(id);
        return Registries.TILE_STATE_REGISTRY.get(name);
    }

    @Override
    public boolean isStoryMode() {
        return this.info.storyMode;
    }

    @Override
    public AbstractEntityPlayer getClosestPlayer(double x, double y, AbstractEntityPlayer excluding) {
        double closestDist = Double.MAX_VALUE;
        AbstractEntityPlayer closestPlayer = null;

        for (AbstractEntityPlayer player : this.players) {
            if (player.world == this && player != excluding) {
                double dist = Util.distanceSq(x, y, player.getX(), player.getY());
                if (closestDist >= dist) {
                    closestDist = dist;
                    closestPlayer = player;
                }
            }
        }

        return closestPlayer;
    }

    @Override
    public AbstractEntityPlayer getClosestPlayer(double x, double y) {
        return this.getClosestPlayer(x, y, null);
    }

    @Override
    public void addPlayer(AbstractEntityPlayer player) {
        if (this.getPlayer(player.getUniqueId()) == null) {
            this.players.add(player);
        } else {
            RockBottomAPI.logger().warning("Tried adding player " + player.getName() + " with id " + player.getUniqueId() + " to world that already contained it!");
        }

        if (this.isServer()) {
            RockBottomAPI.getNet().sendToAllPlayersExcept(this, new PacketPlayer(player, false), player);
        }
    }

    @Override
    public void removePlayer(AbstractEntityPlayer player) {
        this.players.remove(player);

        if (this.isServer()) {
            RockBottomAPI.getNet().sendToAllPlayersExcept(this, new PacketPlayer(player, true), player);
        }
    }

    @Override
    public long getSeed() {
        return this.info.seed;
    }

    @Override
    public WorldInfo getWorldInfo() {
        return this.info;
    }

    @Override
    public NameToIndexInfo getTileRegInfo() {
        return this.regInfo.getTiles();
    }

    @Override
    public int getIdForBiome(Biome biome) {
        ResourceName name = Registries.BIOME_REGISTRY.getId(biome);
        if (name != null) {
            return this.getBiomeRegInfo().getId(name);
        } else {
            return -1;
        }
    }

    @Override
    public Biome getBiomeForId(int id) {
        ResourceName name = this.getBiomeRegInfo().get(id);
        return Registries.BIOME_REGISTRY.get(name);
    }

    @Override
    public NameToIndexInfo getBiomeRegInfo() {
        return this.regInfo.getBiomes();
    }

    @Override
    public DynamicRegistryInfo getRegInfo() {
        return this.regInfo;
    }

    @Override
    public void save() {
        super.save();

        this.info.save();

        for (int i = 0; i < this.players.size(); i++) {
            this.savePlayer(this.players.get(i));
        }
    }

    @Override
    public List<AbstractEntityPlayer> getAllPlayers() {
        return this.playersUnmodifiable;
    }

    @Override
    public void savePlayer(AbstractEntityPlayer player) {
        DataSet playerSet = new DataSet();
        player.save(playerSet, false);

        playerSet.write(new File(this.playerDirectory, player.getUniqueId() + ".dat"));
    }

    @Override
    public Map<ResourceName, IWorldGenerator> getAllGenerators() {
        return this.generators;
    }

    @Override
    public List<IWorldGenerator> getSortedLoopingGenerators() {
        return this.loopingGenerators;
    }

    @Override
    public List<IWorldGenerator> getSortedRetroactiveGenerators() {
        return this.retroactiveGenerators;
    }

    @Override
    public IWorldGenerator getGenerator(ResourceName name) {
        return this.generators.get(name);
    }

    @Override
    public EntityPlayer createPlayer(UUID id, IPlayerDesign design, Channel channel, boolean loadOrSwapLast) {
        EntityPlayer player = channel != null ? new ConnectedPlayer(this, id, design, channel) : new EntityPlayer(this, id, design);

        File file = new File(this.playerDirectory, id + ".dat");
        if (file.exists()) {
            DataSet set = new DataSet();
            set.read(file);

            player.load(set, false);
            RockBottomAPI.logger().info("Loading player " + design.getName() + " with unique id " + id + '!');
        } else {
            boolean loaded = false;

            if (loadOrSwapLast) {
                if (this.info.lastPlayerId != null) {
                    File lastFile = new File(this.playerDirectory, this.info.lastPlayerId + ".dat");
                    if (lastFile.exists()) {
                        DataSet set = new DataSet();
                        set.read(lastFile);

                        player.load(set, false);
                        RockBottomAPI.logger().info("Loading player " + design.getName() + " with unique id " + id + " from last player file " + lastFile + '!');

                        this.savePlayer(player);
                        lastFile.delete();
                        loaded = true;
                    }
                }
            }

            if (!loaded) {
                player.resetAndSpawn(RockBottomAPI.getGame());
                RockBottomAPI.logger().info("Adding new player " + design.getName() + " with unique id " + id + " to world!");
            }
        }

        if (loadOrSwapLast) {
            this.info.lastPlayerId = id;
            this.info.save();
        }

        RockBottomAPI.getEventHandler().fireEvent(new PlayerJoinWorldEvent(player, channel != null));

        return player;
    }

    @Override
    public AbstractEntityPlayer getPlayer(UUID id) {
        for (AbstractEntityPlayer player : this.players) {
            if (id.equals(player.getUniqueId())) {
                return player;
            }
        }
        return null;
    }

    @Override
    public AbstractEntityPlayer getPlayer(String name) {
        for (AbstractEntityPlayer player : this.players) {
            if (name.equals(player.getName())) {
                return player;
            }
        }
        return null;
    }

    @Override
    public int getSpawnX() {
        return 0;
    }

    @Override
    public File getPlayerFolder() {
        return this.playerDirectory;
    }
}
