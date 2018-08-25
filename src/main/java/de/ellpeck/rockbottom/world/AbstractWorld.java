package de.ellpeck.rockbottom.world;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.set.ModBasedDataSet;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.AddEntityToWorldEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldSaveEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldTickEvent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.toast.Toast;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.net.packet.toclient.PacketEntityChange;
import de.ellpeck.rockbottom.net.packet.toclient.PacketParticles;
import de.ellpeck.rockbottom.net.packet.toclient.PacketSound;
import de.ellpeck.rockbottom.net.packet.toclient.PacketTime;
import de.ellpeck.rockbottom.util.thread.ThreadHandler;
import de.ellpeck.rockbottom.world.gen.WorldGenBiomes;
import de.ellpeck.rockbottom.world.gen.WorldGenHeights;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractWorld implements IWorld {

    public final List<IChunk> loadedChunks = new ArrayList<>();
    protected final Table<Integer, Integer, IChunk> chunkLookup = HashBasedTable.create();
    protected ModBasedDataSet additionalData;
    protected File directory;
    protected File chunksDirectory;
    protected File additionalDataFile;
    protected File worldDataFile;
    protected WorldGenBiomes biomeGen;
    protected WorldGenHeights heightGen;
    protected int time;
    protected int totalTime;
    protected boolean timeFrozen;
    protected File persistentChunksFile;

    public AbstractWorld(File worldDirectory) {
        if (worldDirectory != null) {
            this.directory = worldDirectory;
            this.chunksDirectory = new File(worldDirectory, "chunks");
            this.persistentChunksFile = new File(worldDirectory, "persistent_chunks.dat");

            this.additionalDataFile = new File(worldDirectory, "additional_data.dat");
            if (this.additionalDataFile.exists()) {
                this.additionalData = new ModBasedDataSet();
                this.additionalData.read(this.additionalDataFile);
            }

            this.worldDataFile = new File(worldDirectory, "world_data.dat");
            if (this.worldDataFile.exists()) {
                DataSet data = new DataSet();
                data.read(this.worldDataFile);

                this.time = data.getInt("time");
                this.totalTime = data.getInt("total_time");
                this.timeFrozen = data.getBoolean("time_frozen");
            }
        }
    }

    protected void onGeneratorsLoaded() {
        this.biomeGen = Preconditions.checkNotNull((WorldGenBiomes) this.getGenerator(WorldGenBiomes.ID), "The default biome generator for world " + this.getName() + " has been removed from the registry!");
        this.heightGen = Preconditions.checkNotNull((WorldGenHeights) this.getGenerator(WorldGenHeights.ID), "The default heights generator for world " + this.getName() + " has been removed from the registry!");

        Map<Pos2, Boolean> persistentChunks = new HashMap<>();

        if (this.persistentChunksFile != null && this.persistentChunksFile.exists()) {
            DataSet set = new DataSet();
            set.read(this.persistentChunksFile);

            int amount = set.getInt("amount");
            for (int i = 0; i < amount; i++) {
                persistentChunks.put(new Pos2(set.getInt("x_" + i), set.getInt("y_" + i)), false);
            }
        }

        for(Pos2 pos : this.getDefaultPersistentChunks()){
            persistentChunks.put(pos, true);
        }

        for (Map.Entry<Pos2, Boolean> entry : persistentChunks.entrySet()) {
            Pos2 pos = entry.getKey();
            boolean constantPersist = entry.getValue();

            this.loadChunk(pos.getX(), pos.getY(), constantPersist, !constantPersist);

            if (constantPersist) {
                RockBottomAPI.logger().config("Creating constantly persistent chunk at " + pos.getX() + ", " + pos.getY() + " in world " + this.getName());
            } else {
                RockBottomAPI.logger().config("Loading persisting chunk at " + pos.getX() + ", " + pos.getY() + " in world " + this.getName());
            }
        }
    }

    protected abstract List<Pos2> getDefaultPersistentChunks();

    protected void updateChunksAndTime(IGameInstance game) {
        this.totalTime++;

        if (!this.timeFrozen) {
            this.time++;
            if (this.time >= Constants.TIME_PER_DAY) {
                this.time = 0;
            }
        }

        for (int i = this.loadedChunks.size() - 1; i >= 0; i--) {
            IChunk chunk = this.loadedChunks.get(i);
            chunk.update(game);

            if (chunk.shouldUnload()) {
                this.unloadChunk(chunk);
            }
        }
    }

    public boolean update(AbstractGame game) {
        if (RockBottomAPI.getEventHandler().fireEvent(new WorldTickEvent(this)) != EventResult.CANCELLED) {
            this.updateChunksAndTime(game);

            if (this.isServer() && this.totalTime % 80 == 0) {
                RockBottomAPI.getNet().sendToAllPlayers(this, new PacketTime(this.time, this.totalTime, this.timeFrozen));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addEntity(Entity entity) {
        AddEntityToWorldEvent event = new AddEntityToWorldEvent(this, entity);
        if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
            entity = event.entity;

            double x = entity.getX();
            double y = entity.getY();

            IChunk chunk = this.getChunk(x, y);
            chunk.addEntity(entity);

            if (!chunk.isGenerating() && this.isServer()) {
                RockBottomAPI.getNet().sendToAllPlayersWithLoadedPosExcept(this, new PacketEntityChange(entity, false), x, y, entity);
            }
        }
    }

    @Override
    public void removeEntity(Entity entity, IChunk chunk) {
        chunk.removeEntity(entity);
        entity.onRemoveFromWorld();

        if (this.isServer()) {
            RockBottomAPI.getNet().sendToAllPlayersWithLoadedPosExcept(this, new PacketEntityChange(entity, true), chunk.getX(), chunk.getY(), entity);
        }
    }

    @Override
    public void addTileEntity(TileEntity tile) {
        IChunk chunk = this.getChunk(tile.x, tile.y);
        chunk.addTileEntity(tile);
    }

    @Override
    public void removeEntity(Entity entity) {
        IChunk chunk = this.getChunk(entity.getX(), entity.getY());
        this.removeEntity(entity, chunk);
    }

    @Override
    public void removeTileEntity(TileLayer layer, int x, int y) {
        IChunk chunk = this.getChunk(x, y);
        chunk.removeTileEntity(layer, x, y);
    }

    @Override
    public TileEntity getTileEntity(TileLayer layer, int x, int y) {
        IChunk chunk = this.getChunk(x, y);
        return chunk.getTileEntity(layer, x, y);
    }

    @Override
    public TileEntity getTileEntity(int x, int y) {
        return this.getTileEntity(TileLayer.MAIN, x, y);
    }

    @Override
    public <T extends TileEntity> T getTileEntity(TileLayer layer, int x, int y, Class<T> tileClass) {
        IChunk chunk = this.getChunk(x, y);
        return chunk.getTileEntity(layer, x, y, tileClass);
    }

    @Override
    public <T extends TileEntity> T getTileEntity(int x, int y, Class<T> tileClass) {
        return this.getTileEntity(TileLayer.MAIN, x, y, tileClass);
    }

    @Override
    public void reevaluateTickBehavior(TileEntity tile) {
        IChunk chunk = this.getChunk(tile.x, tile.y);
        chunk.reevaluateTickBehavior(tile);
    }

    @Override
    public List<Entity> getAllEntities() {
        List<Entity> entities = new ArrayList<>();
        for (IChunk chunk : this.loadedChunks) {
            entities.addAll(chunk.getAllEntities());
        }
        return entities;
    }

    @Override
    public int getCurrentTime() {
        return this.time;
    }

    @Override
    public void setCurrentTime(int time) {
        this.time = time;
    }

    @Override
    public int getTotalTime() {
        return this.totalTime;
    }

    @Override
    public void setTotalTime(int time) {
        this.totalTime = time;
    }

    @Override
    public boolean isTimeFrozen() {
        return this.timeFrozen;
    }

    @Override
    public void setTimeFrozen(boolean timeFrozen) {
        this.timeFrozen = timeFrozen;
    }

    @Override
    public boolean isDaytime() {
        float light = this.getSkylightModifier(true);
        return light >= 0.7F;
    }

    @Override
    public boolean isNighttime() {
        return !this.isDaytime();
    }

    @Override
    public List<TileEntity> getAllTileEntities() {
        List<TileEntity> tiles = new ArrayList<>();
        for (IChunk chunk : this.loadedChunks) {
            tiles.addAll(chunk.getAllTileEntities());
        }
        return tiles;
    }

    @Override
    public List<TileEntity> getAllTickingTileEntities() {
        List<TileEntity> tiles = new ArrayList<>();
        for (IChunk chunk : this.loadedChunks) {
            tiles.addAll(chunk.getAllTickingTileEntities());
        }
        return tiles;
    }

    @Override
    public Entity getEntity(UUID id) {
        for (IChunk chunk : this.loadedChunks) {
            Entity entity = chunk.getEntity(id);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public List<Entity> getEntities(BoundBox area) {
        return this.getEntities(area, null, null);
    }

    @Override
    public List<Entity> getEntities(BoundBox area, Predicate<Entity> test) {
        return this.getEntities(area, null, test);
    }

    @Override
    public <T extends Entity> List<T> getEntities(BoundBox area, Class<T> type) {
        return this.getEntities(area, type, null);
    }

    @Override
    public <T extends Entity> List<T> getEntities(BoundBox area, Class<T> type, Predicate<T> test) {
        int minChunkX = Util.toGridPos(area.getMinX() - Constants.CHUNK_SIZE / 2);
        int minChunkY = Util.toGridPos(area.getMinY() - Constants.CHUNK_SIZE / 2);
        int maxChunkX = Util.toGridPos(area.getMaxX() + Constants.CHUNK_SIZE / 2);
        int maxChunkY = Util.toGridPos(area.getMaxY() + Constants.CHUNK_SIZE / 2);

        List<T> entities = new ArrayList<>();
        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int y = minChunkY; y <= maxChunkY; y++) {
                if (this.isChunkLoaded(x, y)) {
                    IChunk chunk = this.getChunkFromGridCoords(x, y);
                    entities.addAll(chunk.getEntities(area, type, test));
                }
            }
        }
        return entities;
    }

    @Override
    public byte getCombinedLight(int x, int y) {
        IChunk chunk = this.getChunk(x, y);
        return chunk.getCombinedLight(x, y);
    }

    @Override
    public byte getCombinedVisualLight(int x, int y) {
        byte light = this.getCombinedLight(x, y);

        if (!this.isDedicatedServer()) {
            AbstractEntityPlayer player = RockBottomAPI.getGame().getPlayer();
            double dist = Util.distanceSq(x + 0.5D, y, player.getX(), player.getY());
            if (dist <= 20D) {
                byte newLight = (byte) (0.35D * (20D - dist));
                if (light < newLight) {
                    light = newLight;
                }
            }
        }

        return light;
    }

    @Override
    public byte getSkyLight(int x, int y) {
        IChunk chunk = this.getChunk(x, y);
        return chunk.getSkyLight(x, y);
    }

    @Override
    public byte getArtificialLight(int x, int y) {
        IChunk chunk = this.getChunk(x, y);
        return chunk.getArtificialLight(x, y);
    }

    @Override
    public void setSkyLight(int x, int y, byte light) {
        IChunk chunk = this.getChunk(x, y);
        chunk.setSkyLight(x, y, light);
    }

    @Override
    public void setArtificialLight(int x, int y, byte light) {
        IChunk chunk = this.getChunk(x, y);
        chunk.setArtificialLight(x, y, light);
    }

    @Override
    public void scheduleUpdate(int x, int y, TileLayer layer, int scheduledMeta, int time) {
        IChunk chunk = this.getChunk(x, y);
        chunk.scheduleUpdate(x, y, layer, scheduledMeta, time);
    }

    @Override
    public boolean isChunkLoaded(int x, int y) {
        return this.isChunkLoaded(x, y, true);
    }

    @Override
    public boolean isChunkLoaded(int x, int y, boolean checkGenerating) {
        IChunk chunk = this.chunkLookup.get(x, y);
        return chunk != null && (!checkGenerating || !chunk.isGenerating());
    }

    @Override
    public boolean isPosLoaded(double x, double y) {
        return this.isPosLoaded(x, y, true);
    }

    @Override
    public boolean isPosLoaded(double x, double y, boolean checkGenerating) {
        return this.isChunkLoaded(Util.toGridPos(x), Util.toGridPos(y), checkGenerating);
    }

    @Override
    public void scheduleUpdate(int x, int y, TileLayer layer, int time) {
        this.scheduleUpdate(x, y, layer, 0, time);
    }

    @Override
    public void setDirty(int x, int y) {
        IChunk chunk = this.getChunk(x, y);
        chunk.setDirty(x, y);
    }

    @Override
    public int getChunkHeight(TileLayer layer, int x, int bottomY) {
        IChunk chunk = this.getChunk(x, bottomY);
        return chunk.getChunkHeight(layer, x, bottomY);
    }

    @Override
    public int getAverageChunkHeight(TileLayer layer, int x, int bottomY) {
        IChunk chunk = this.getChunk(x, bottomY);
        return chunk.getAverageChunkHeight(layer, x, bottomY);
    }

    @Override
    public float getChunkFlatness(TileLayer layer, int x, int y) {
        IChunk chunk = this.getChunk(x, y);
        return chunk.getChunkFlatness(layer, x, y);
    }

    @Override
    public Biome getExpectedBiome(int x, int y) {
        return this.biomeGen.getBiome(this, x, y, this.getExpectedSurfaceHeight(TileLayer.MAIN, x));
    }

    @Override
    public BiomeLevel getExpectedBiomeLevel(int x, int y) {
        return this.biomeGen.getSmoothedLevelForPos(this, x, y, this.getExpectedSurfaceHeight(TileLayer.MAIN, x));
    }

    @Override
    public int getExpectedSurfaceHeight(TileLayer layer, int x) {
        return this.heightGen.getHeight(layer, x);
    }

    @Override
    public int getExpectedAverageHeight(TileLayer layer, int startX, int endX) {
        int totalHeight = 0;
        for (int checkX = startX; checkX < endX; checkX++) {
            totalHeight += this.getExpectedSurfaceHeight(layer, checkX);
        }
        return totalHeight / Constants.CHUNK_SIZE;
    }

    @Override
    public float getExpectedSurfaceFlatness(TileLayer layer, int startX, int endX) {
        Set<Integer> uniqueHeights = new HashSet<>();
        for (int checkX = startX; checkX < endX; checkX++) {
            uniqueHeights.add(this.getExpectedSurfaceHeight(layer, checkX));
        }
        return 1F - (uniqueHeights.size() - 1F) / (Constants.CHUNK_SIZE - 1F);
    }

    @Override
    public INoiseGen getNoiseGenForBiome(Biome biome) {
        return this.biomeGen.getBiomeNoise(this, biome);
    }

    @Override
    public Biome getBiome(int x, int y) {
        IChunk chunk = this.getChunk(x, y);
        return chunk.getBiome(x, y);
    }

    @Override
    public void setBiome(int x, int y, Biome biome) {
        IChunk chunk = this.getChunk(x, y);
        chunk.setBiome(x, y, biome);
    }

    @Override
    public boolean isClient() {
        return RockBottomAPI.getNet().isClient();
    }

    @Override
    public boolean isServer() {
        return RockBottomAPI.getNet().isServer();
    }

    @Override
    public boolean isDedicatedServer() {
        return RockBottomAPI.getGame().isDedicatedServer();
    }

    @Override
    public boolean isLocalPlayer(Entity entity) {
        return RockBottomAPI.getNet().isThePlayer(entity);
    }

    @Override
    public void callRetroactiveGeneration() {
        for (IChunk chunk : this.loadedChunks) {
            chunk.callRetroactiveGeneration();
        }
    }

    @Override
    public IChunk getChunk(double x, double y) {
        return this.getChunkFromGridCoords(Util.toGridPos(x), Util.toGridPos(y));
    }

    @Override
    public IChunk getChunkFromGridCoords(int gridX, int gridY) {
        IChunk chunk = this.chunkLookup.get(gridX, gridY);

        if (chunk == null) {
            chunk = this.loadChunk(gridX, gridY, false, true);
        }

        return chunk;
    }

    protected Chunk loadChunk(int gridX, int gridY, boolean isPersistent, boolean enqueue) {
        Chunk chunk = new Chunk(this, gridX, gridY, isPersistent);
        this.loadedChunks.add(chunk);
        this.chunkLookup.put(gridX, gridY, chunk);

        Runnable r = () -> {
            DataSet set = new DataSet();
            set.read(new File(this.chunksDirectory, "c_" + gridX + '_' + gridY + ".dat"));
            chunk.loadOrCreate(set);
        };

        if (enqueue) {
            ThreadHandler.chunkGenThread.add(r);
        } else {
            r.run();
        }

        return chunk;
    }

    @Override
    public void unloadChunk(IChunk chunk) {
        this.saveChunk(chunk, true);

        this.loadedChunks.remove(chunk);
        this.chunkLookup.remove(chunk.getGridX(), chunk.getGridY());
    }

    @Override
    public TileState getState(int x, int y) {
        return this.getState(TileLayer.MAIN, x, y);
    }

    @Override
    public TileState getState(TileLayer layer, int x, int y) {
        IChunk chunk = this.getChunk(x, y);
        return chunk.getState(layer, x, y);
    }

    @Override
    public void setState(int x, int y, TileState tile) {
        this.setState(TileLayer.MAIN, x, y, tile);
    }

    @Override
    public void setState(TileLayer layer, int x, int y, TileState tile) {
        IChunk chunk = this.getChunk(x, y);
        chunk.setState(layer, x, y, tile);
    }

    @Override
    public void notifyNeighborsOfChange(int x, int y, TileLayer layer) {
        for (Direction direction : Direction.ADJACENT_INCLUDING_NONE) {
            int offX = x + direction.x;
            int offY = y + direction.y;

            if (this.isPosLoaded(offX, offY)) {
                for (TileLayer other : TileLayer.getAllLayers()) {
                    if (direction != Direction.NONE || layer != other) {
                        this.getState(other, offX, offY).getTile().onChangeAround(this, offX, offY, other, x, y, layer);
                    }
                }
            }
        }
    }

    @Override
    public void save() {
        ThreadHandler.chunkGenThread.add(() -> {
            long timeStarted = Util.getTimeMillis();
            int amount = 0;

            RockBottomAPI.getEventHandler().fireEvent(new WorldSaveEvent(this, RockBottomAPI.getGame().getDataManager()));

            DataSet data = new DataSet();
            data.addInt("time", this.time);
            data.addInt("total_time", this.totalTime);
            data.addBoolean("time_frozen", this.timeFrozen);
            data.write(this.worldDataFile);

            List<Pos2> persistentChunks = new ArrayList<>();

            for (int i = 0; i < this.loadedChunks.size(); i++) {
                IChunk chunk = this.loadedChunks.get(i);

                if (this.saveChunk(chunk, false)) {
                    amount++;
                }

                if (chunk.doesEntityForcePersistence()) {
                    persistentChunks.add(new Pos2(chunk.getGridX(), chunk.getGridY()));
                }
            }

            DataSet set = new DataSet();
            if (!persistentChunks.isEmpty()) {
                set.addInt("amount", persistentChunks.size());

                int counter = 0;
                for (Pos2 pos : persistentChunks) {
                    set.addInt("x_" + counter, pos.getX());
                    set.addInt("y_" + counter, pos.getY());
                    counter++;
                }
            }
            set.write(this.persistentChunksFile);

            if (this.additionalData != null) {
                this.additionalData.write(this.additionalDataFile);
            }

            if (amount > 0) {
                long time = Util.getTimeMillis() - timeStarted;
                RockBottomAPI.logger().info("Saved " + amount + " chunks for world "+this.getName()+", took " + time + "ms.");

                if (!this.isDedicatedServer()) {
                    IGameInstance game = RockBottomAPI.getGame();

                    int finalAmount = amount;
                    game.enqueueAction((g, o) -> game.getToaster().displayToast(new Toast(ResourceName.intern("gui.save_world"), new ChatComponentTranslation(ResourceName.intern("info.saved")), new ChatComponentTranslation(ResourceName.intern("info.saved_chunks"), String.valueOf(finalAmount), String.valueOf((float) time / 1000F)), 160)), null);
                }
            }
        });
    }

    @Override
    public String getName() {
        return this.directory.getName();
    }

    @Override
    public File getFolder() {
        return this.directory;
    }

    @Override
    public File getChunksFolder() {
        return this.chunksDirectory;
    }

    @Override
    public void playSound(AbstractEntityPlayer player, ResourceName name, double x, double y, double z, float pitch, float volume) {
        if (this.isLocalPlayer(player)) {
            RockBottomAPI.getGame().getAssetManager().getSound(name).playAt(pitch, volume, x, y, z);
        } else {
            player.sendPacket(new PacketSound(name, x, y, z, pitch, volume));
        }
    }

    @Override
    public void broadcastSound(AbstractEntityPlayer player, ResourceName name, float pitch, float volume) {
        if (this.isLocalPlayer(player)) {
            RockBottomAPI.getGame().getAssetManager().getSound(name).play(pitch, volume);
        } else {
            player.sendPacket(new PacketSound(name, pitch, volume));
        }
    }

    @Override
    public void playSound(ResourceName name, double x, double y, double z, float pitch, float volume, AbstractEntityPlayer except) {
        if (this.isServer()) {
            RockBottomAPI.getNet().sendToAllPlayersWithLoadedPosExcept(this, new PacketSound(name, x, y, z, pitch, volume), x, y, except);
        }

        if (!this.isDedicatedServer() && !this.isLocalPlayer(except)) {
            RockBottomAPI.getGame().getAssetManager().getSound(name).playAt(pitch, volume, x, y, z);
        }
    }

    @Override
    public void broadcastSound(ResourceName name, float pitch, float volume, AbstractEntityPlayer except) {
        if (this.isServer()) {
            RockBottomAPI.getNet().sendToAllPlayersExcept(this, new PacketSound(name, pitch, volume), except);
        }

        if (!this.isDedicatedServer() && !this.isLocalPlayer(except)) {
            RockBottomAPI.getGame().getAssetManager().getSound(name).play(pitch, volume);
        }
    }

    @Override
    public void playSound(ResourceName name, double x, double y, double z, float pitch, float volume) {
        this.playSound(name, x, y, z, pitch, volume, null);
    }

    @Override
    public void broadcastSound(ResourceName name, float pitch, float volume) {
        this.broadcastSound(name, pitch, volume, null);
    }

    protected boolean saveChunk(IChunk chunk, boolean enqueue) {
        if (chunk.needsSave()) {
            Runnable r = () -> {
                DataSet set = new DataSet();
                chunk.save(set);
                set.write(new File(this.chunksDirectory, "c_" + chunk.getGridX() + '_' + chunk.getGridY() + ".dat"));
            };

            if (enqueue) {
                ThreadHandler.chunkGenThread.add(r);
            } else {
                r.run();
            }

            return true;
        }
        return false;
    }

    @Override
    public void destroyTile(int x, int y, TileLayer layer, Entity destroyer, boolean shouldDrop) {
        TileState state = this.getState(layer, x, y);

        state.getTile().onDestroyed(this, x, y, destroyer, layer, shouldDrop);

        if (this.isServer()) {
            RockBottomAPI.getNet().sendToAllPlayersWithLoadedPos(this, PacketParticles.tile(this, x, y, state), x, y);
        }

        if (!this.isDedicatedServer()) {
            RockBottomAPI.getGame().getParticleManager().addTileParticles(this, x, y, state);
        }

        ResourceName sound = state.getTile().getBreakSound(this, x, y, layer, destroyer);
        if (sound != null) {
            this.playSound(sound, x + 0.5, y + 0.5, layer.index(), 1F, 1F);
        }

        this.setState(layer, x, y, GameContent.TILE_AIR.getDefState());
    }

    @Override
    public void causeLightUpdate(int x, int y) {
        ThreadHandler.lightingThread.add(() -> {
            for (Direction direction : Direction.SURROUNDING_INCLUDING_NONE) {
                int dirX = x + direction.x;
                int dirY = y + direction.y;

                if (this.isPosLoaded(dirX, dirY)) {
                    boolean change = false;

                    byte skylightThere = this.getSkyLight(dirX, dirY);
                    byte calcedSkylight = this.calcLight(dirX, dirY, true, true);
                    if (calcedSkylight != skylightThere) {
                        this.setSkyLight(dirX, dirY, calcedSkylight);
                        change = true;
                    }

                    byte artLightThere = this.getArtificialLight(dirX, dirY);
                    byte calcedArtLight = this.calcLight(dirX, dirY, false, true);
                    if (calcedArtLight != artLightThere) {
                        this.setArtificialLight(dirX, dirY, calcedArtLight);
                        change = true;
                    }

                    if (change) {
                        this.causeLightUpdate(dirX, dirY);
                    }
                }
            }
        });
    }

    public void calcInitialSkylight(int x1, int y1, int x2, int y2) {
        for (int x = x2; x >= x1; x--) {
            for (int y = y2; y >= y1; y--) {
                byte light = this.calcLight(x, y, true, false);
                this.setSkyLight(x, y, light);
            }
        }

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                byte light = this.calcLight(x, y, true, false);
                this.setSkyLight(x, y, light);
            }
        }
    }

    private byte calcLight(int x, int y, boolean isSky, boolean checkGenerating) {
        byte maxLight = 0;

        for (Direction direction : Direction.SURROUNDING) {
            int dirX = x + direction.x;
            int dirY = y + direction.y;

            if (this.isPosLoaded(dirX, dirY, checkGenerating)) {
                byte light = isSky ? this.getSkyLight(dirX, dirY) : this.getArtificialLight(dirX, dirY);
                if (light > maxLight) {
                    maxLight = light;
                }
            }
        }

        maxLight *= this.getTileModifier(x, y, isSky);

        byte emitted = this.getTileLight(x, y, isSky);
        if (emitted > maxLight) {
            maxLight = emitted;
        }

        return (byte) Math.min(Constants.MAX_LIGHT, maxLight);
    }

    private byte getTileLight(int x, int y, boolean isSky) {
        int highestLight = 0;
        boolean nonAir = false;

        for (TileLayer layer : TileLayer.getAllLayers()) {
            Tile tile = this.getState(layer, x, y).getTile();
            if (!tile.isAir()) {
                int light = tile.getLight(this, x, y, layer);
                if (light > highestLight) {
                    highestLight = light;
                }

                nonAir = true;
            }
        }

        if (nonAir) {
            if (!isSky) {
                return (byte) highestLight;
            }
        } else if (isSky) {
            return Constants.MAX_LIGHT;
        }
        return 0;
    }

    private float getTileModifier(int x, int y, boolean isSky) {
        float smallestMod = 1F;
        boolean nonAir = false;

        for (TileLayer layer : TileLayer.getAllLayers()) {
            Tile tile = this.getState(layer, x, y).getTile();
            if (!tile.isAir()) {
                float mod = tile.getTranslucentModifier(this, x, y, layer, isSky);
                if (mod < smallestMod) {
                    smallestMod = mod;
                }

                nonAir = true;
            }
        }

        if (nonAir) {
            return smallestMod;
        } else {
            return isSky ? 1F : 0.8F;
        }
    }

    public float getSkylightModifier(boolean doMinMax) {
        float mod = ((float) Math.sin(2 * Math.PI * ((double) this.time / (double) Constants.TIME_PER_DAY)) + 1F) / 2F;

        if (doMinMax) {
            return Math.min(1F, mod + 0.15F);
        } else {
            return mod;
        }
    }

    @Override
    public boolean hasAdditionalData() {
        return this.additionalData != null;
    }

    @Override
    public ModBasedDataSet getAdditionalData() {
        return this.additionalData;
    }

    @Override
    public void setAdditionalData(ModBasedDataSet set) {
        this.additionalData = set;
    }

    @Override
    public ModBasedDataSet getOrCreateAdditionalData() {
        if (this.additionalData == null) {
            this.additionalData = new ModBasedDataSet();
        }
        return this.additionalData;
    }
}
