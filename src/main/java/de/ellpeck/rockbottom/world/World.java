package de.ellpeck.rockbottom.world;

import com.google.common.base.Preconditions;
import de.ellpeck.rockbottom.api.*;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.impl.PlayerJoinWorldEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldLoadEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldUnloadEvent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.toast.Toast;
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
import de.ellpeck.rockbottom.net.packet.toclient.PacketPlayer;
import de.ellpeck.rockbottom.net.server.ConnectedPlayer;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.gen.WorldGenBiomes;
import de.ellpeck.rockbottom.world.gen.WorldGenHeights;
import io.netty.channel.Channel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class World extends AbstractWorld {

    public final List<AbstractEntityPlayer> players = new ArrayList<>();
    protected final List<AbstractEntityPlayer> playersUnmodifiable = Collections.unmodifiableList(this.players);
    private final List<SubWorld> subWorlds;
    private final DynamicRegistryInfo regInfo;
    protected File playerDirectory;
    protected int saveTicksCounter;
    protected final WorldInfo info;

    public World(WorldInfo info, DynamicRegistryInfo regInfo, File worldDirectory, boolean isClient) {
        super(worldDirectory, info.seed);
        this.regInfo = regInfo;
        this.info = info;

        if (worldDirectory != null) {
            this.playerDirectory = new File(worldDirectory, "players");
        }

        if (!isClient) {
            this.initializeGenerators();
            this.loadPersistentChunks();

            List<SubWorld> subs = new ArrayList<>();
            for (SubWorldInitializer initializer : Registries.SUB_WORLD_INITIALIZER_REGISTRY.values()) {
                ResourceName name = initializer.getWorldName();
                Preconditions.checkNotNull(name, "The world name of sub world initializer " + initializer.getName() + " is null - this is not allowed!");

                SubWorld world = new SubWorld(this, name, initializer);

                world.initializeGenerators();
                initializer.onGeneratorsInitialized(world);

                world.loadPersistentChunks();

                RockBottomAPI.getEventHandler().fireEvent(new WorldLoadEvent(world, this.info, this.regInfo));
                subs.add(world);

                RockBottomAPI.logger().info("Initialized sub world " + world.getName() + " for world " + this.getName());
            }
            this.subWorlds = Collections.unmodifiableList(subs);

            RockBottomAPI.logger().info("Initialized a total of " + this.subWorlds.size() + " sub worlds for world " + this.getName());
        } else {
            this.subWorlds = null;
        }
    }

    @Override
    protected BiomeGen getBiomeGen() {
        return Preconditions.checkNotNull((WorldGenBiomes) this.getGenerator(WorldGenBiomes.ID), "The default biome generator has been removed from the registry!");
    }

    @Override
    protected HeightGen getHeightGen() {
        return Preconditions.checkNotNull((WorldGenHeights) this.getGenerator(WorldGenHeights.ID), "The default heights generator has been removed from the registry!");
    }

    @Override
    public boolean renderSky(IGameInstance game, IAssetManager manager, IRenderer g, AbstractWorld world, AbstractEntityPlayer player, double width, double height) {
        return true;
    }

    @Override
    protected List<Pos2> getDefaultPersistentChunks() {
        List<Pos2> list = new ArrayList<>();
        int dist = Constants.PERSISTENT_CHUNK_DISTANCE;
        for (int x = -dist; x <= dist; x++) {
            for (int y = -dist; y <= dist; y++) {
                list.add(new Pos2(x, y));
            }
        }
        return list;
    }

    @Override
    protected boolean shouldGenerateHere(IWorldGenerator generator, ResourceName name) {
        return generator.shouldExistInWorld(this);
    }

    @Override
    public boolean update(AbstractGame game) {
        if (super.update(game)) {
            this.saveTicksCounter++;
            if (this.saveTicksCounter >= game.getAutosaveInterval() * Constants.TARGET_TPS) {
                this.saveTicksCounter = 0;

                this.save();
            }

            for (SubWorld world : this.subWorlds) {
                world.update(game);
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
            if (player != excluding) {
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
    public void travelToSubWorld(Entity entity, ResourceName subWorld, double x, double y) {
        AbstractWorld world = this.getSubWorld(subWorld);
        if (world != null && world != entity.world) {
            entity.world.removeEntity(entity);

            entity.setPos(x, y);
            entity.moveToWorld(world);

            world.addEntity(entity);

            RockBottomAPI.logger().fine("Entity " + entity + " travelling from world " + this.getName() + " to world " + world.getName() + " (" + x + ", " + y + ')');
        }
    }

    @Override
    public AbstractWorld getSubWorld(ResourceName name) {
        if (name == null) {
            return this;
        } else {
            for (SubWorld world : this.subWorlds) {
                if (world.getSubName().equals(name)) {
                    return world;
                }
            }
            return null;
        }
    }

    @Override
    public List<? extends IWorld> getSubWorlds() {
        return this.subWorlds;
    }

    @Override
    public IWorld getMainWorld() {
        return this;
    }

    @Override
    public ResourceName getSubName() {
        return null;
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
    public List<AbstractEntityPlayer> getAllPlayers() {
        return this.playersUnmodifiable;
    }

    @Override
    public File getPlayerFolder() {
        return this.playerDirectory;
    }

    @Override
    public void savePlayer(AbstractEntityPlayer player) {
        DataSet playerSet = new DataSet();
        player.save(playerSet, false);

        ResourceName sub = player.world.getSubName();
        if (sub != null) {
            playerSet.addString("world", sub.toString());
        }

        playerSet.write(new File(this.playerDirectory, player.getUniqueId() + ".dat"));
    }

    @Override
    public EntityPlayer createPlayer(UUID id, IPlayerDesign design, Channel channel, boolean loadOrSwapLast) {
        EntityPlayer player = null;

        File file = new File(this.playerDirectory, id + ".dat");
        if (file.exists()) {
            player = this.loadPlayer(file, id, design, channel);
            RockBottomAPI.logger().info("Loading player " + design.getName() + " with unique id " + id + '!');
        } else {
            boolean loaded = false;

            if (loadOrSwapLast) {
                if (this.info.lastPlayerId != null) {
                    File lastFile = new File(this.playerDirectory, this.info.lastPlayerId + ".dat");
                    if (lastFile.exists()) {
                        player = this.loadPlayer(lastFile, id, design, channel);
                        RockBottomAPI.logger().info("Loading player " + design.getName() + " with unique id " + id + " from last player file " + lastFile + '!');

                        this.savePlayer(player);
                        lastFile.delete();
                        loaded = true;
                    }
                }
            }

            if (!loaded) {
                player = makePlayer(this, id, design, channel);
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

    private EntityPlayer loadPlayer(File file, UUID id, IPlayerDesign design, Channel channel) {
        DataSet set = new DataSet();
        set.read(file);

        IWorld world = null;
        if (set.hasKey("world")) {
            world = this.getSubWorld(new ResourceName(set.getString("world")));
        }
        if (world == null) {
            world = this;
        }

        EntityPlayer player = makePlayer(world, id, design, channel);
        player.load(set, false);
        return player;
    }

    private static EntityPlayer makePlayer(IWorld world, UUID id, IPlayerDesign design, Channel channel) {
        return channel != null ? new ConnectedPlayer(world, id, design, channel) : new EntityPlayer(world, id, design);
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
    protected void saveImpl(long startTime) {
        this.info.save();

        for (int i = 0; i < this.players.size(); i++) {
            this.savePlayer(this.players.get(i));
        }

        for (SubWorld world : this.subWorlds) {
            world.save();
        }

        long time = Util.getTimeMillis() - startTime;
        RockBottomAPI.logger().info("Saved world " + this.getName() + ", took " + time + "ms.");

        if (!this.isDedicatedServer()) {
            IGameInstance game = RockBottomAPI.getGame();
            game.enqueueAction((g, o) -> game.getToaster().displayToast(new Toast(ResourceName.intern("gui.save_world"), new ChatComponentTranslation(ResourceName.intern("info.saved")), new ChatComponentTranslation(ResourceName.intern("info.saved_world"), String.valueOf((float) time / 1000F)), 160)), null);
        }
    }

    public void onUnloaded() {
        RockBottomAPI.getEventHandler().fireEvent(new WorldUnloadEvent(this));

        for (SubWorld world : this.subWorlds) {
            RockBottomAPI.getEventHandler().fireEvent(new WorldUnloadEvent(world));
        }
    }
}
