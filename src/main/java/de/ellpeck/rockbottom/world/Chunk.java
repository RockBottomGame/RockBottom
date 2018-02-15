package de.ellpeck.rockbottom.world;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.ChunkSaveEvent;
import de.ellpeck.rockbottom.api.event.impl.EntityTickEvent;
import de.ellpeck.rockbottom.api.event.impl.TileEntityTickEvent;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Counter;
import de.ellpeck.rockbottom.api.util.Pos3;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.net.packet.toclient.PacketEntityChange;
import de.ellpeck.rockbottom.net.packet.toclient.PacketScheduledUpdate;
import de.ellpeck.rockbottom.net.packet.toclient.PacketTileChange;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.*;
import java.util.function.Predicate;

public class Chunk implements IChunk{

    public static boolean isGeneratingChunk;

    public final int x;
    public final int y;

    public final int gridX;
    public final int gridY;
    public final List<AbstractEntityPlayer> playersInRange = new ArrayList<>();
    public final List<AbstractEntityPlayer> playersOutOfRangeCached = new ArrayList<>();
    public final Map<AbstractEntityPlayer, Counter> playersOutOfRangeCachedTimers = new HashMap<>();
    protected final World world;
    protected final Biome[][] biomeGrid = new Biome[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    protected final Map<TileLayer, TileState[][]> stateGrid = new TreeMap<>(Comparator.comparing(TileLayer:: getName));
    protected final byte[][][] lightGrid = new byte[2][Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    protected final List<Entity> entities = new ArrayList<>();
    protected final Map<UUID, Entity> entityLookup = new HashMap<>();
    protected final List<TileEntity> tileEntities = new ArrayList<>();
    protected final Map<Pos3, TileEntity> tileEntityLookup = new HashMap<>();
    protected final List<TileEntity> tickingTileEntities = new ArrayList<>();
    protected final List<ScheduledUpdate> scheduledUpdates = new ArrayList<>();
    protected final Map<Pos3, ScheduledUpdate> scheduledUpdateLookup = new HashMap<>();
    protected final List<Entity> entitiesUnmodifiable;
    protected final List<TileEntity> tileEntitiesUnmodifiable;
    protected final List<TileEntity> tickingTileEntitiesUnmodifiable;
    public boolean isGenerating;
    protected boolean needsSave;
    private int internalLoadingTimer;
    private DataSet additionalData;

    public Chunk(World world, int gridX, int gridY){
        this.world = world;

        this.x = Util.toWorldPos(gridX);
        this.y = Util.toWorldPos(gridY);
        this.gridX = gridX;
        this.gridY = gridY;

        this.isGenerating = true;
        this.internalLoadingTimer = Constants.CHUNK_LOAD_TIME;

        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                this.biomeGrid[x][y] = GameContent.BIOME_SKY;
            }
        }

        this.entitiesUnmodifiable = Collections.unmodifiableList(this.entities);
        this.tileEntitiesUnmodifiable = Collections.unmodifiableList(this.tileEntities);
        this.tickingTileEntitiesUnmodifiable = Collections.unmodifiableList(this.tickingTileEntities);
    }

    private void generate(List<? extends IWorldGenerator> gens){
        if(isGeneratingChunk){
            RockBottomAPI.logger().warning("CHUNK GEN BLEEDING INTO DIFFERENT CHUNK AT "+this.gridX+", "+this.gridY+"! THIS SHOULD NOT HAPPEN!");
        }

        isGeneratingChunk = true;

        for(IWorldGenerator generator : gens){
            if(this.canGenerate(generator) && generator.shouldGenerate(this.world, this)){
                generator.generate(this.world, this);
            }
        }

        isGeneratingChunk = false;
    }

    private boolean canGenerate(IWorldGenerator generator){
        if(generator.needsPlayerToAllowGeneration(this.world, this)){
            for(AbstractEntityPlayer player : this.world.players){
                if(generator.doesPlayerAllowGeneration(this.world, this, player)){
                    return true;
                }
            }
            return false;
        }
        else{
            return true;
        }
    }

    protected void checkListSync(){
        if(this.entities.size() != this.entityLookup.size()){
            throw new IllegalStateException("Entities and EntityLookup are out of sync!");
        }
        if(this.tileEntities.size() != this.tileEntityLookup.size()){
            throw new IllegalStateException("TileEntities and TileEntityLookup are out of sync!");
        }
        if(this.tickingTileEntities.size() > this.tileEntities.size()){
            throw new IllegalStateException("There are more ticking TileEntities than there are normal ones!");
        }
        if(this.scheduledUpdates.size() != this.scheduledUpdateLookup.size()){
            throw new IllegalStateException("ScheduledUpdates and ScheduledUpdateLookup are out of sync!");
        }
        if(this.playersOutOfRangeCached.size() != this.playersOutOfRangeCachedTimers.size()){
            throw new IllegalStateException("PlayersOutOfRangeCached and PlayersOutOfRangeCachedTimers are out of sync!");
        }
    }

    protected void updateEntities(IGameInstance game){
        for(int i = 0; i < this.entities.size(); i++){
            Entity entity = this.entities.get(i);

            if(RockBottomAPI.getEventHandler().fireEvent(new EntityTickEvent(entity)) != EventResult.CANCELLED){
                entity.update(game);
            }

            if(entity.shouldBeRemoved()){
                this.world.removeEntity(entity, this);
                i--;
            }
            else{
                int newChunkX = Util.toGridPos(entity.x);
                int newChunkY = Util.toGridPos(entity.y);

                if(newChunkX != this.gridX || newChunkY != this.gridY){
                    this.removeEntity(entity);
                    i--;

                    IChunk chunk = this.world.getChunkFromGridCoords(newChunkX, newChunkY);
                    chunk.addEntity(entity);

                    if(this.world.isServer()){
                        for(AbstractEntityPlayer player : chunk.getPlayersInRange()){
                            if(!this.playersInRange.contains(player)){
                                player.sendPacket(new PacketEntityChange(entity, false));

                                RockBottomAPI.logger().config("Adding entity "+entity+" with id "+entity.getUniqueId()+" to chunk in range of player with id "+player.getUniqueId());
                            }
                        }
                    }
                }
            }
        }

        for(int i = 0; i < this.tickingTileEntities.size(); i++){
            TileEntity tile = this.tickingTileEntities.get(i);

            if(RockBottomAPI.getEventHandler().fireEvent(new TileEntityTickEvent(tile)) != EventResult.CANCELLED){
                tile.update(game);
            }

            if(tile.shouldRemove()){
                this.removeTileEntity(tile.layer, tile.x, tile.y);
                i--;
            }
        }
    }

    @Override
    public void update(IGameInstance game){
        this.checkListSync();

        if(!this.isGenerating){
            this.updateEntities(game);

            int layers = TileLayer.getAllLayers().size();
            for(int i = 0; i < Constants.RANDOM_TILE_UPDATES*layers; i++){
                TileLayer layer = TileLayer.getAllLayers().get(Util.RANDOM.nextInt(layers));
                int randX = Util.RANDOM.nextInt(Constants.CHUNK_SIZE);
                int randY = Util.RANDOM.nextInt(Constants.CHUNK_SIZE);

                Tile tile = this.getStateInner(layer, randX, randY).getTile();
                tile.updateRandomly(this.world, this.x+randX, this.y+randY, layer);
            }

            if(!this.scheduledUpdates.isEmpty()){
                for(int i = 0; i < this.scheduledUpdates.size(); i++){
                    ScheduledUpdate update = this.scheduledUpdates.get(i);
                    update.time--;

                    if(update.time <= 0){
                        this.scheduledUpdates.remove(i);
                        this.scheduledUpdateLookup.remove(new Pos3(update.x, update.y, update.layer.index()));

                        Tile tile = this.getState(update.layer, update.x, update.y).getTile();
                        if(tile == update.tile.getTile()){
                            tile.onScheduledUpdate(this.world, update.x, update.y, update.layer, update.scheduledMeta);

                            if(this.world.isServer()){
                                RockBottomAPI.getNet().sendToAllPlayersWithLoadedPos(this.world, new PacketScheduledUpdate(update.layer, update.x, update.y, update.scheduledMeta), update.x, update.y);
                            }
                        }

                        i--;
                        this.setDirty();
                    }
                }
            }
        }

        if(this.internalLoadingTimer > 0){
            this.internalLoadingTimer--;
        }

        for(int i = 0; i < this.playersOutOfRangeCached.size(); i++){
            AbstractEntityPlayer player = this.playersOutOfRangeCached.get(i);

            Counter time = this.playersOutOfRangeCachedTimers.get(player);
            time.add(-1);

            if(time.get() <= 0){
                player.getChunksInRange().remove(this);
                player.onChunkUnloaded(this);

                this.playersOutOfRangeCached.remove(i);
                this.playersOutOfRangeCachedTimers.remove(player);

                i--;
            }
        }
    }

    @Override
    public TileState getState(int x, int y){
        return this.getState(TileLayer.MAIN, x, y);
    }

    @Override
    public TileState getState(TileLayer layer, int x, int y){
        return this.getStateInner(layer, x-this.x, y-this.y);
    }

    @Override
    public void setState(int x, int y, TileState tile){
        this.setState(TileLayer.MAIN, x, y, tile);
    }

    @Override
    public void setState(TileLayer layer, int x, int y, TileState tile){
        this.setStateInner(layer, x-this.x, y-this.y, tile);
    }

    @Override
    public TileState getStateInner(TileLayer layer, int x, int y){
        TileState[][] grid = this.getGrid(layer, false);

        if(grid != null){
            return grid[x][y];
        }
        else{
            return GameContent.TILE_AIR.getDefState();
        }
    }

    @Override
    public TileState getStateInner(int x, int y){
        return this.getStateInner(TileLayer.MAIN, x, y);
    }

    @Override
    public void setStateInner(int x, int y, TileState tile){
        this.setStateInner(TileLayer.MAIN, x, y, tile);
    }

    @Override
    public void setStateInner(TileLayer layer, int x, int y, TileState tile){
        if(tile == null){
            throw new IllegalArgumentException("Tried setting null tile in chunk at "+this.gridX+", "+this.gridY+"!");
        }

        Tile newTile = tile.getTile();
        if(!layer.canTileBeInLayer(this.world, this.x+x, this.y+y, newTile)){
            throw new UnsupportedOperationException("Tried setting tile "+tile+" at "+(this.x+x)+", "+(this.y+y)+" on layer "+layer+" that doesn't allow it!");
        }

        Tile lastTile = this.getStateInner(layer, x, y).getTile();
        if(newTile != lastTile){
            lastTile.onRemoved(this.world, this.x+x, this.y+y, layer);

            if(layer.canHoldTileEntities() && lastTile.canProvideTileEntity()){
                this.removeTileEntity(layer, this.x+x, this.y+y);
            }
        }

        TileState[][] grid = this.getGrid(layer, !newTile.isAir());
        if(grid != null){
            grid[x][y] = tile;
        }

        if(this.world.isServer()){
            RockBottomAPI.getNet().sendToAllPlayersWithLoadedPos(this.world, new PacketTileChange(this.x+x, this.y+y, layer, this.world.getIdForState(tile)), this.x+x, this.y+y);
        }

        if(newTile != lastTile){
            if(layer.canHoldTileEntities() && newTile.canProvideTileEntity()){
                TileEntity tileEntity = newTile.provideTileEntity(this.world, this.x+x, this.y+y, layer);
                if(tileEntity != null){
                    this.addTileEntity(tileEntity);
                }
            }

            newTile.onAdded(this.world, this.x+x, this.y+y, layer);
        }

        if(!this.isGenerating){
            this.world.causeLightUpdate(this.x+x, this.y+y);

            this.world.notifyNeighborsOfChange(this.x+x, this.y+y, layer);
            this.setDirty();
        }
    }

    @Override
    public void addEntity(Entity entity){
        if(this.entityLookup.containsKey(entity.getUniqueId())){
            RockBottomAPI.logger().warning("Tried adding entity "+entity+" with id "+entity.getUniqueId()+" to chunk at "+this.gridX+", "+this.gridY+" that already contained it!");
        }
        else{
            this.entities.add(entity);
            this.entityLookup.put(entity.getUniqueId(), entity);

            entity.moveToChunk(this);

            if(!this.isGenerating){
                this.setDirty();
            }
        }
    }

    @Override
    public void addTileEntity(TileEntity tile){
        if(!tile.layer.canHoldTileEntities()){
            throw new UnsupportedOperationException("Tried adding tile entity "+tile+" at "+tile.x+", "+tile.y+" on layer "+tile.layer+" that doesn't allow tile entities!");
        }

        Pos3 posVec = new Pos3(tile.x, tile.y, tile.layer.index());
        if(!this.tileEntityLookup.containsKey(posVec)){
            this.tileEntities.add(tile);
            this.tileEntityLookup.put(posVec, tile);

            if(tile.doesTick()){
                this.tickingTileEntities.add(tile);
            }

            if(!this.isGenerating){
                this.world.notifyNeighborsOfChange(tile.x, tile.y, tile.layer);
                this.setDirty();
            }
        }
    }

    @Override
    public void removeEntity(Entity entity){
        this.entities.remove(entity);
        this.entityLookup.remove(entity.getUniqueId());

        if(!this.isGenerating){
            this.setDirty();
        }
    }

    @Override
    public void removeTileEntity(TileLayer layer, int x, int y){
        TileEntity tile = this.getTileEntity(layer, x, y);
        if(tile != null){
            this.tileEntities.remove(tile);
            this.tileEntityLookup.remove(new Pos3(tile.x, tile.y, tile.layer.index()));

            if(tile.doesTick()){
                this.tickingTileEntities.remove(tile);
            }

            if(!this.isGenerating){
                this.world.notifyNeighborsOfChange(this.x+x, this.y+y, tile.layer);
                this.setDirty();
            }
        }
    }

    @Override
    public TileEntity getTileEntity(TileLayer layer, int x, int y){
        return this.tileEntityLookup.get(new Pos3(x, y, layer.index()));
    }

    @Override
    public TileEntity getTileEntity(int x, int y){
        return this.getTileEntity(TileLayer.MAIN, x, y);
    }

    @Override
    public <T extends TileEntity> T getTileEntity(TileLayer layer, int x, int y, Class<T> tileClass){
        TileEntity tile = this.getTileEntity(layer, x, y);
        if(tile != null && tileClass.isAssignableFrom(tile.getClass())){
            return (T)tile;
        }
        else{
            return null;
        }
    }

    @Override
    public <T extends TileEntity> T getTileEntity(int x, int y, Class<T> tileClass){
        return this.getTileEntity(TileLayer.MAIN, x, y, tileClass);
    }

    @Override
    public void reevaluateTickBehavior(TileEntity tile){
        if(tile.doesTick()){
            if(!this.tickingTileEntities.contains(tile)){
                this.tickingTileEntities.add(tile);
            }
        }
        else{
            this.tickingTileEntities.remove(tile);
        }
    }

    @Override
    public List<Entity> getAllEntities(){
        return this.entitiesUnmodifiable;
    }

    @Override
    public List<TileEntity> getAllTileEntities(){
        return this.tileEntitiesUnmodifiable;
    }

    @Override
    public List<TileEntity> getAllTickingTileEntities(){
        return this.tickingTileEntitiesUnmodifiable;
    }

    @Override
    public Entity getEntity(UUID id){
        return this.entityLookup.get(id);
    }

    @Override
    public List<Entity> getEntities(BoundBox area){
        return this.getEntities(area, null, null);
    }

    @Override
    public List<Entity> getEntities(BoundBox area, Predicate<Entity> test){
        return this.getEntities(area, null, test);
    }

    @Override
    public <T extends Entity> List<T> getEntities(BoundBox area, Class<T> type){
        return this.getEntities(area, type, null);
    }

    @Override
    public <T extends Entity> List<T> getEntities(BoundBox area, Class<T> type, Predicate<T> test){
        List<T> entities = new ArrayList<>();

        for(Entity entity : this.entities){
            if(!entity.isDead() && (type == null || type.isAssignableFrom(entity.getClass()))){
                T castEntity = (T)entity;
                if(test == null || test.test(castEntity)){
                    if(entity.getBoundingBox().copy().add(entity.x, entity.y).intersects(area)){
                        entities.add(castEntity);
                    }
                }
            }
        }

        return entities;
    }

    @Override
    public byte getCombinedLight(int x, int y){
        return this.getCombinedLightInner(x-this.x, y-this.y);
    }

    @Override
    public byte getSkyLight(int x, int y){
        return this.getSkylightInner(x-this.x, y-this.y);
    }

    @Override
    public byte getArtificialLight(int x, int y){
        return this.getArtificialLightInner(x-this.x, y-this.y);
    }

    @Override
    public void setSkyLight(int x, int y, byte light){
        this.setSkylightInner(x-this.x, y-this.y, light);
    }

    @Override
    public void setArtificialLight(int x, int y, byte light){
        this.setArtificialLightInner(x-this.x, y-this.y, light);
    }

    @Override
    public void scheduleUpdate(int x, int y, TileLayer layer, int scheduledMeta, int time){
        Pos3 posVec = new Pos3(x, y, layer.index());
        if(!this.scheduledUpdateLookup.containsKey(posVec)){
            ScheduledUpdate update = new ScheduledUpdate(x, y, layer, this.getState(layer, x, y), scheduledMeta, time);

            this.scheduledUpdateLookup.put(posVec, update);
            this.scheduledUpdates.add(update);

            if(!this.isGenerating){
                this.setDirty();
            }
        }
    }

    @Override
    public void scheduleUpdate(int x, int y, TileLayer layer, int time){
        this.scheduleUpdate(x, y, layer, 0, time);
    }

    @Override
    public void setDirty(int x, int y){
        this.setDirty();
    }

    @Override
    public int getLowestAirUpwards(TileLayer layer, int x, int y){
        return this.getLowestAirUpwards(layer, x, y, false);
    }

    @Override
    public int getLowestAirUpwards(TileLayer layer, int x, int y, boolean ignoreReplaceableTiles){
        int result = this.getLowestAirUpwardsInner(layer, x-this.x, y-this.y, ignoreReplaceableTiles);
        if(result >= 0){
            return this.y+result;
        }
        else{
            return -1;
        }
    }

    @Override
    public int getLowestAirUpwardsInner(TileLayer layer, int x, int y){
        return this.getLowestAirUpwardsInner(layer, x, y, false);
    }

    @Override
    public int getLowestAirUpwardsInner(TileLayer layer, int x, int y, boolean ignoreReplaceableTiles){
        for(int yCount = y; yCount < Constants.CHUNK_SIZE-yCount; yCount++){
            Tile tile = this.getStateInner(layer, x, yCount).getTile();
            if(tile.isAir() || (ignoreReplaceableTiles && tile.canReplace(this.world, this.x+x, this.y+y, layer))){
                return yCount;
            }
        }
        return -1;
    }

    @Override
    public Biome getBiome(int x, int y){
        return this.getBiomeInner(x-this.x, y-this.y);
    }

    @Override
    public void setBiome(int x, int y, Biome biome){
        this.setBiomeInner(x-this.x, y-this.y, biome);
    }

    @Override
    public boolean isClient(){
        return this.world.isClient();
    }

    @Override
    public boolean isServer(){
        return this.world.isServer();
    }

    @Override
    public boolean isDedicatedServer(){
        return this.world.isDedicatedServer();
    }

    @Override
    public boolean isLocalPlayer(Entity entity){
        return this.world.isLocalPlayer(entity);
    }

    @Override
    public void callRetroactiveGeneration(){
        this.generate(this.world.getSortedRetroactiveGenerators());
    }

    @Override
    public long getSeed(){
        return this.world.getSeed();
    }

    @Override
    public byte getCombinedLightInner(int x, int y){
        byte artificial = this.getArtificialLightInner(x, y);
        byte sky = (byte)(this.getSkylightInner(x, y)*this.world.getSkylightModifier(true));

        return (byte)Math.min(Constants.MAX_LIGHT, artificial+sky);
    }

    @Override
    public byte getSkylightInner(int x, int y){
        return this.lightGrid[0][x][y];
    }

    @Override
    public void setSkylightInner(int x, int y, byte light){
        this.lightGrid[0][x][y] = light;

        if(!this.isGenerating){
            this.setDirty();
        }
    }

    @Override
    public byte getArtificialLightInner(int x, int y){
        return this.lightGrid[1][x][y];
    }

    @Override
    public void setArtificialLightInner(int x, int y, byte light){
        this.lightGrid[1][x][y] = light;

        if(!this.isGenerating){
            this.setDirty();
        }
    }

    @Override
    public void setGenerating(boolean generating){
        this.isGenerating = generating;
    }

    @Override
    public boolean needsSave(){
        return this.needsSave;
    }

    @Override
    public boolean shouldUnload(){
        return this.internalLoadingTimer <= 0 && this.playersInRange.isEmpty() && this.playersOutOfRangeCached.isEmpty();
    }

    public void setDirty(){
        this.needsSave = true;
    }

    @Override
    public void save(DataSet set){
        RockBottomAPI.getEventHandler().fireEvent(new ChunkSaveEvent(this, RockBottomAPI.getGame().getDataManager()));

        int layerCounter = 0;
        for(TileLayer layer : this.stateGrid.keySet()){
            int[] ids = new int[Constants.CHUNK_SIZE*Constants.CHUNK_SIZE];

            int counter = 0;
            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    ids[counter] = this.world.getIdForState(this.getStateInner(layer, x, y));
                    counter++;
                }
            }

            set.addIntArray("l_"+layerCounter, ids);
            set.addString("ln_"+layerCounter, layer.getName().toString());

            layerCounter++;
        }
        set.addInt("l_a", layerCounter);

        short[] biomes = new short[Constants.CHUNK_SIZE*Constants.CHUNK_SIZE];
        int biomeCounter = 0;
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                biomes[biomeCounter] = (short)this.world.getIdForBiome(this.getBiomeInner(x, y));
                biomeCounter++;
            }
        }
        set.addShortArray("bi", biomes);


        for(int i = 0; i < this.lightGrid.length; i++){
            byte[] light = new byte[Constants.CHUNK_SIZE*Constants.CHUNK_SIZE];
            int counter = 0;
            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    light[counter] = this.lightGrid[i][x][y];
                    counter++;
                }
            }

            set.addByteArray("lg_"+i, light);
        }

        int entityId = 0;
        for(Entity entity : this.entities){
            if(entity.doesSave() && !(entity instanceof EntityPlayer)){
                DataSet entitySet = new DataSet();
                entitySet.addString("name", RockBottomAPI.ENTITY_REGISTRY.getId(entity.getClass()).toString());
                entity.save(entitySet);

                set.addDataSet("e_"+entityId, entitySet);

                entityId++;
            }
        }
        set.addInt("e_a", entityId);

        int tileEntityId = 0;
        for(TileEntity tile : this.tileEntities){
            if(tile.doesSave()){
                DataSet tileSet = new DataSet();
                tileSet.addInt("x", tile.x);
                tileSet.addInt("y", tile.y);
                tileSet.addString("layer", tile.layer.getName().toString());
                tile.save(tileSet, false);

                set.addDataSet("t_"+tileEntityId, tileSet);

                tileEntityId++;
            }
        }
        set.addInt("t_a", tileEntityId);

        DataSet updateSet = new DataSet();

        int updateId = 0;
        for(ScheduledUpdate update : this.scheduledUpdates){
            updateSet.addInt("x_"+updateId, update.x);
            updateSet.addInt("y_"+updateId, update.y);
            updateSet.addString("l_"+updateId, update.layer.getName().toString());
            updateSet.addInt("m_"+updateId, update.scheduledMeta);
            updateSet.addInt("t_"+updateId, update.time);
            updateSet.addInt("i_"+updateId, this.world.getIdForState(update.tile));

            updateId++;
        }
        updateSet.addInt("a", updateId);

        set.addDataSet("s_u", updateSet);

        if(this.additionalData != null){
            set.addDataSet("ad_da", this.additionalData);
        }

        this.needsSave = false;
    }

    public void loadOrCreate(DataSet set){
        this.isGenerating = true;

        if(set != null && !set.isEmpty()){
            int layerAmount = set.getInt("l_a");

            for(int i = 0; i < layerAmount; i++){
                IResourceName res = RockBottomAPI.createRes(set.getString("ln_"+i));
                TileLayer layer = RockBottomAPI.TILE_LAYER_REGISTRY.get(res);
                if(layer != null){
                    int[] ids = set.getIntArray("l_"+i, Constants.CHUNK_SIZE*Constants.CHUNK_SIZE);

                    int counter = 0;
                    for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                        for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                            TileState tile = this.world.getStateForId(ids[counter]);
                            if(tile != null){
                                this.setStateInner(layer, x, y, tile);
                            }
                            else{
                                RockBottomAPI.logger().warning("Could not load tile at "+x+" "+y+" because id "+ids[counter]+" is missing!");
                            }
                            counter++;
                        }
                    }
                }
                else{
                    RockBottomAPI.logger().warning("Could not load tile layer with name "+res+" as it is missing!");
                }
            }

            short[] biomes = set.getShortArray("bi", Constants.CHUNK_SIZE*Constants.CHUNK_SIZE);
            int biomeCounter = 0;
            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    Biome biome = this.world.getBiomeForId(biomes[biomeCounter]);
                    if(biome != null){
                        this.setBiomeInner(x, y, biome);
                    }
                    else{
                        RockBottomAPI.logger().warning("Could not load biome at "+x+" "+y+" because id "+biomes[biomeCounter]+" is missing!");
                    }
                    biomeCounter++;
                }
            }

            for(int i = 0; i < this.lightGrid.length; i++){
                byte[] light = set.getByteArray("lg_"+i, Constants.CHUNK_SIZE*Constants.CHUNK_SIZE);
                int counter = 0;
                for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                    for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                        this.lightGrid[i][x][y] = light[counter];
                        counter++;
                    }
                }
            }

            int entityAmount = set.getInt("e_a");
            for(int i = 0; i < entityAmount; i++){
                DataSet entitySet = set.getDataSet("e_"+i);

                String name = entitySet.getString("name");
                Entity entity = Util.createEntity(RockBottomAPI.createRes(name), this.world);

                if(entity != null){
                    entity.load(entitySet);
                    this.addEntity(entity);
                }
                else{
                    RockBottomAPI.logger().warning("Couldn't load entity with name "+name+" and data "+entitySet);
                }
            }

            int tileEntityAmount = set.getInt("t_a");
            for(int i = 0; i < tileEntityAmount; i++){
                DataSet tileSet = set.getDataSet("t_"+i);
                int x = tileSet.getInt("x");
                int y = tileSet.getInt("y");

                IResourceName res = RockBottomAPI.createRes(tileSet.getString("layer"));
                TileLayer layer = RockBottomAPI.TILE_LAYER_REGISTRY.get(res);
                if(layer != null){
                    TileEntity tile = this.getTileEntity(layer, x, y);
                    if(tile != null){
                        tile.load(tileSet, false);
                    }
                    else{
                        RockBottomAPI.logger().warning("Couldn't load data of tile entity at "+x+", "+y+" because it is missing!");
                    }
                }
                else{
                    RockBottomAPI.logger().warning("Could not tile entity at "+x+" "+y+" because layer with name "+res+" is missing!");
                }
            }

            DataSet updateSet = set.getDataSet("s_u");

            int updateAmount = updateSet.getInt("a");
            for(int i = 0; i < updateAmount; i++){
                int x = updateSet.getInt("x_"+i);
                int y = updateSet.getInt("y_"+i);
                int meta = updateSet.getInt("m_"+i);
                int time = updateSet.getInt("t_"+i);

                int id = updateSet.getInt("i_"+i);
                TileState tile = this.world.getStateForId(id);

                if(tile != null){
                    IResourceName res = RockBottomAPI.createRes(updateSet.getString("l_"+i));
                    TileLayer layer = RockBottomAPI.TILE_LAYER_REGISTRY.get(res);
                    if(layer != null){
                        this.scheduleUpdate(x, y, layer, meta, time);
                    }
                    else{
                        RockBottomAPI.logger().warning("Could not load scheduled update at "+x+" "+y+" with time "+time+" because layer with name "+res+" is missing!");
                    }
                }
                else{
                    RockBottomAPI.logger().warning("Could not load scheduled update at "+x+" "+y+" with time "+time+" because tile with id "+id+" is missing!");
                }
            }

            if(set.hasKey("ad_da")){
                this.additionalData = set.getDataSet("ad_da");
            }

            this.callRetroactiveGeneration();
        }
        else{
            this.generate(this.world.getSortedGenerators());
            this.world.calcInitialSkylight(this.x, this.y, this.x+Constants.CHUNK_SIZE-1, this.y+Constants.CHUNK_SIZE-1);
        }

        this.isGenerating = false;
    }

    @Override
    public int getScheduledUpdateAmount(){
        return this.scheduledUpdates.size();
    }

    @Override
    public Biome getBiomeInner(int x, int y){
        return this.biomeGrid[x][y];
    }

    @Override
    public void setBiomeInner(int x, int y, Biome biome){
        if(biome == null){
            throw new IllegalArgumentException("Tried setting null biome in chunk at "+this.gridX+", "+this.gridY+"!");
        }

        this.biomeGrid[x][y] = biome;
    }

    @Override
    public Set<TileLayer> getLoadedLayers(){
        return this.stateGrid.keySet();
    }

    @Override
    public List<AbstractEntityPlayer> getPlayersInRange(){
        return this.playersInRange;
    }

    @Override
    public List<AbstractEntityPlayer> getPlayersLeftRange(){
        return this.playersOutOfRangeCached;
    }

    @Override
    public Map<AbstractEntityPlayer, Counter> getLeftPlayerTimers(){
        return this.playersOutOfRangeCachedTimers;
    }

    @Override
    public int getGridX(){
        return this.gridX;
    }

    @Override
    public int getGridY(){
        return this.gridY;
    }

    @Override
    public IWorld getWorld(){
        return this.world;
    }

    @Override
    public int getX(){
        return this.x;
    }

    @Override
    public int getY(){
        return this.y;
    }

    private TileState[][] getGrid(TileLayer layer, boolean create){
        TileState[][] grid = this.stateGrid.get(layer);

        if(grid == null && create){
            grid = new TileState[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
            this.stateGrid.put(layer, grid);

            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    grid[x][y] = GameContent.TILE_AIR.getDefState();
                }
            }
        }

        return grid;
    }

    @Override
    public boolean hasAdditionalData(){
        return this.additionalData != null;
    }

    @Override
    public DataSet getAdditionalData(){
        return this.additionalData;
    }

    @Override
    public void setAdditionalData(DataSet set){
        this.additionalData = set;
    }

    @Override
    public DataSet getOrCreateAdditionalData(){
        if(this.additionalData == null){
            this.additionalData = new DataSet();
        }
        return this.additionalData;
    }

    protected static class ScheduledUpdate{

        public final int x;
        public final int y;
        public final TileLayer layer;
        public final TileState tile;
        public final int scheduledMeta;

        public int time;

        public ScheduledUpdate(int x, int y, TileLayer layer, TileState tile, int scheduledMeta, int time){
            this.x = x;
            this.y = y;
            this.layer = layer;
            this.tile = tile;
            this.scheduledMeta = scheduledMeta;

            this.time = time;
        }
    }
}
