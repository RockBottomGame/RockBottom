package de.ellpeck.rockbottom.world;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.EntityTickEvent;
import de.ellpeck.rockbottom.api.event.impl.TileEntityTickEvent;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.MutableInt;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Pos3;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.net.packet.toclient.PacketEntityChange;
import de.ellpeck.rockbottom.net.packet.toclient.PacketMetaChange;
import de.ellpeck.rockbottom.net.packet.toclient.PacketTileChange;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.world.gen.WorldGenerators;
import org.newdawn.slick.util.Log;

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
    public final Map<AbstractEntityPlayer, MutableInt> playersOutOfRangeCachedTimers = new HashMap<>();
    protected final World world;
    protected final Tile[][][] tileGrid = new Tile[TileLayer.LAYERS.length][Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    protected final byte[][][] metaGrid = new byte[TileLayer.LAYERS.length][Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    protected final byte[][][] lightGrid = new byte[2][Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    protected final List<Entity> entities = new ArrayList<>();
    protected final Map<UUID, Entity> entityLookup = new HashMap<>();
    protected final List<TileEntity> tileEntities = new ArrayList<>();
    protected final Map<Pos2, TileEntity> tileEntityLookup = new HashMap<>();
    protected final List<ScheduledUpdate> scheduledUpdates = new ArrayList<>();
    protected final Map<Pos3, ScheduledUpdate> scheduledUpdateLookup = new HashMap<>();
    public boolean isGenerating;
    protected boolean needsSave;
    private int internalLoadingTimer;

    public Chunk(World world, int gridX, int gridY){
        this.world = world;

        this.x = Util.toWorldPos(gridX);
        this.y = Util.toWorldPos(gridY);
        this.gridX = gridX;
        this.gridY = gridY;

        this.isGenerating = true;
        this.internalLoadingTimer = 200;

        for(int i = 0; i < TileLayer.LAYERS.length; i++){
            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    this.tileGrid[i][x][y] = ContentRegistry.TILE_AIR;
                }
            }
        }
    }

    public void generate(Random rand){
        if(isGeneratingChunk){
            Log.warn("CHUNK GEN BLEEDING INTO DIFFERENT CHUNK AT "+this.gridX+", "+this.gridY+"! THIS SHOULD NOT HAPPEN!");
        }

        isGeneratingChunk = true;

        List<IWorldGenerator> gens = WorldGenerators.getGenerators();

        for(IWorldGenerator generator : gens){
            if(generator.shouldGenerate(this.world, this, rand)){
                generator.generate(this.world, this, rand);
            }
        }

        isGeneratingChunk = false;
    }

    protected void checkListSync(){
        if(this.entities.size() != this.entityLookup.size()){
            throw new IllegalStateException("Entities and EntityLookup are out of sync!");
        }
        if(this.tileEntities.size() != this.tileEntityLookup.size()){
            throw new IllegalStateException("TileEntities and TileEntityLookup are out of sync!");
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
                this.world.removeEntity(entity);
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

                    if(RockBottomAPI.getNet().isServer()){
                        for(AbstractEntityPlayer player : chunk.getPlayersInRange()){
                            if(!this.playersInRange.contains(player)){
                                player.sendPacket(new PacketEntityChange(entity, false));

                                Log.debug("Adding entity "+entity+" with id "+entity.getUniqueId()+" to chunk in range of player with id "+player.getUniqueId());
                            }
                        }
                    }
                }
            }
        }

        for(int i = 0; i < this.tileEntities.size(); i++){
            TileEntity tile = this.tileEntities.get(i);

            if(RockBottomAPI.getEventHandler().fireEvent(new TileEntityTickEvent(tile)) != EventResult.CANCELLED){
                tile.update(game);
            }

            if(tile.shouldRemove()){
                this.removeTileEntity(tile.x, tile.y);
                i--;
            }
        }
    }

    @Override
    public void update(IGameInstance game){
        this.checkListSync();

        if(!this.isGenerating){
            this.updateEntities(game);

            for(int i = 0; i < Constants.RANDOM_TILE_UPDATES; i++){
                int randX = Util.RANDOM.nextInt(Constants.CHUNK_SIZE);
                int randY = Util.RANDOM.nextInt(Constants.CHUNK_SIZE);

                Tile tile = this.getTileInner(randX, randY);
                tile.updateRandomly(this.world, this.x+randX, this.y+randY);
            }

            if(!this.scheduledUpdates.isEmpty()){
                for(int i = 0; i < this.scheduledUpdates.size(); i++){
                    ScheduledUpdate update = this.scheduledUpdates.get(i);
                    update.time--;

                    if(update.time <= 0){
                        this.scheduledUpdates.remove(i);
                        this.scheduledUpdateLookup.remove(new Pos3(update.x, update.y, update.layer.ordinal()));

                        Tile tile = this.getTile(update.layer, update.x, update.y);
                        if(tile == update.tile){
                            tile.onScheduledUpdate(this.world, update.x, update.y, update.layer);
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

            MutableInt time = this.playersOutOfRangeCachedTimers.get(player);
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
    public Tile getTile(int x, int y){
        return this.getTile(TileLayer.MAIN, x, y);
    }

    @Override
    public Tile getTile(TileLayer layer, int x, int y){
        return this.getTileInner(layer, x-this.x, y-this.y);
    }

    @Override
    public int getMeta(int x, int y){
        return this.getMeta(TileLayer.MAIN, x, y);
    }

    @Override
    public int getMeta(TileLayer layer, int x, int y){
        return this.getMetaInner(layer, x-this.x, y-this.y);
    }

    @Override
    public void setTile(int x, int y, Tile tile){
        this.setTile(TileLayer.MAIN, x, y, tile);
    }

    @Override
    public void setTile(TileLayer layer, int x, int y, Tile tile){
        this.setTileInner(layer, x-this.x, y-this.y, tile, 0);
    }

    @Override
    public void setTile(int x, int y, Tile tile, int meta){
        this.setTileInner(TileLayer.MAIN, x-this.x, y-this.y, tile, meta);
    }

    @Override
    public void setTile(TileLayer layer, int x, int y, Tile tile, int meta){
        this.setTileInner(layer, x-this.x, y-this.y, tile, meta);
    }

    @Override
    public void setMeta(int x, int y, int meta){
        this.setMeta(TileLayer.MAIN, x, y, meta);
    }

    @Override
    public void setMeta(TileLayer layer, int x, int y, int meta){
        this.setMetaInner(layer, x-this.x, y-this.y, meta);
    }

    @Override
    public Tile getTileInner(TileLayer layer, int x, int y){
        return this.tileGrid[layer.ordinal()][x][y];
    }

    @Override
    public Tile getTileInner(int x, int y){
        return this.getTileInner(TileLayer.MAIN, x, y);
    }

    @Override
    public byte getMetaInner(TileLayer layer, int x, int y){
        return this.metaGrid[layer.ordinal()][x][y];
    }

    @Override
    public void setTileInner(int x, int y, Tile tile, int meta){
        this.setTileInner(TileLayer.MAIN, x, y, tile, meta);
    }

    @Override
    public void setTileInner(int x, int y, Tile tile){
        this.setTileInner(TileLayer.MAIN, x, y, tile);
    }

    @Override
    public void setTileInner(TileLayer layer, int x, int y, Tile tile){
        this.setTileInner(layer, x, y, tile, 0);
    }

    @Override
    public void setTileInner(TileLayer layer, int x, int y, Tile tile, int meta){
        if(tile == null){
            throw new IllegalArgumentException("Tried setting null tile in chunk at "+this.gridX+", "+this.gridY+"!");
        }

        Tile lastTile = this.getTileInner(layer, x, y);

        boolean lastAir = lastTile.isAir();
        int lastLight = lastTile.getLight(this.world, this.x+x, this.y+y, layer);
        float lastMofifier = lastTile.getTranslucentModifier(this.world, this.x+x, this.y+y, layer);

        lastTile.onRemoved(this.world, this.x+x, this.y+y, layer);

        if(layer == TileLayer.MAIN){
            if(lastTile.canProvideTileEntity()){
                this.removeTileEntity(this.x+x, this.y+y);
            }
        }

        int ord = layer.ordinal();
        this.tileGrid[ord][x][y] = tile;
        this.setMetaFast(ord, x, y, meta);

        tile.onAdded(this.world, this.x+x, this.y+y, layer);

        if(layer == TileLayer.MAIN){
            if(tile.canProvideTileEntity()){
                TileEntity tileEntity = tile.provideTileEntity(this.world, this.x+x, this.y+y);
                if(tileEntity != null){
                    this.addTileEntity(tileEntity);
                }
            }
        }

        if(RockBottomAPI.getNet().isServer()){
            RockBottomAPI.getNet().sendToAllPlayers(this.world, new PacketTileChange(this.x+x, this.y+y, layer, this.world.getIdForTile(tile), meta));
        }

        if(!this.isGenerating){
            if(lastAir != tile.isAir() || lastLight != tile.getLight(this.world, this.x+x, this.y+y, layer) || lastMofifier != tile.getTranslucentModifier(this.world, this.x+x, this.y+y, layer)){
                this.world.causeLightUpdate(this.x+x, this.y+y);
            }

            this.world.notifyNeighborsOfChange(this.x+x, this.y+y, layer);
            this.setDirty();
        }
    }

    private void setMetaFast(int layer, int x, int y, int meta){
        if(meta < 0 || meta > Byte.MAX_VALUE){
            throw new IndexOutOfBoundsException("Tried assigning meta "+meta+" in chunk at "+this.gridX+", "+this.gridY+" which is less than 0 or greater than max "+Byte.MAX_VALUE+"!");
        }

        this.metaGrid[layer][x][y] = (byte)meta;
    }

    @Override
    public void setMetaInner(TileLayer layer, int x, int y, int meta){
        this.setMetaFast(layer.ordinal(), x, y, meta);

        if(RockBottomAPI.getNet().isServer()){
            RockBottomAPI.getNet().sendToAllPlayers(this.world, new PacketMetaChange(this.x+x, this.y+y, layer, meta));
        }

        if(!this.isGenerating){
            this.world.notifyNeighborsOfChange(this.x+x, this.y+y, layer);
            this.setDirty();
        }
    }

    @Override
    public void addEntity(Entity entity){
        if(this.entityLookup.containsKey(entity.getUniqueId())){
            Log.error("Tried adding entity "+entity+" with id "+entity.getUniqueId()+" to chunk at "+this.gridX+", "+this.gridY+" that already contained it!");
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
        Pos2 posVec = new Pos2(tile.x, tile.y);
        if(!this.tileEntityLookup.containsKey(posVec)){
            this.tileEntities.add(tile);
            this.tileEntityLookup.put(posVec, tile);

            if(!this.isGenerating){
                this.world.notifyNeighborsOfChange(tile.x, tile.y, TileLayer.MAIN);
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
    public void removeTileEntity(int x, int y){
        TileEntity tile = this.getTileEntity(x, y);
        if(tile != null){
            this.tileEntities.remove(tile);
            this.tileEntityLookup.remove(new Pos2(tile.x, tile.y));

            if(!this.isGenerating){
                this.world.notifyNeighborsOfChange(this.x+x, this.y+y, TileLayer.MAIN);
                this.setDirty();
            }
        }
    }

    @Override
    public TileEntity getTileEntity(int x, int y){
        return this.tileEntityLookup.get(new Pos2(x, y));
    }

    @Override
    public <T extends TileEntity> T getTileEntity(int x, int y, Class<T> tileClass){
        TileEntity tile = this.getTileEntity(x, y);
        if(tile != null && tileClass.isAssignableFrom(tile.getClass())){
            return (T)tile;
        }
        else{
            return null;
        }
    }

    @Override
    public List<Entity> getAllEntities(){
        return this.entities;
    }

    @Override
    public List<TileEntity> getAllTileEntities(){
        return this.tileEntities;
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
    public void scheduleUpdate(int x, int y, TileLayer layer, int time){
        Pos3 posVec = new Pos3(x, y, layer.ordinal());
        if(!this.scheduledUpdateLookup.containsKey(posVec)){
            ScheduledUpdate update = new ScheduledUpdate(x, y, layer, this.getTile(layer, x, y), time);

            this.scheduledUpdateLookup.put(posVec, update);
            this.scheduledUpdates.add(update);

            if(!this.isGenerating){
                this.setDirty();
            }
        }
    }

    @Override
    public void setDirty(int x, int y){
        this.setDirty();
    }

    @Override
    public int getLowestAirUpwards(TileLayer layer, int x, int y){
        int actualX = x-this.x;
        int actualY = y-this.y;

        for(int yCount = actualY; yCount < Constants.CHUNK_SIZE-yCount; yCount++){
            Tile tile = this.getTileInner(layer, actualX, yCount);
            if(tile.isAir()){
                return this.y+yCount;
            }
        }

        return -1;
    }

    @Override
    public byte getCombinedLightInner(int x, int y){
        byte artificial = this.getArtificialLightInner(x, y);
        byte sky = (byte)(this.getSkylightInner(x, y)*this.world.getSkylightModifier());

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
        return this.internalLoadingTimer <= 0 &&  this.playersInRange.isEmpty() && this.playersOutOfRangeCached.isEmpty();
    }

    public void setDirty(){
        this.needsSave = true;
    }

    @Override
    public void save(DataSet set){
        for(int i = 0; i < TileLayer.LAYERS.length; i++){
            TileLayer layer = TileLayer.LAYERS[i];
            short[][] ids = new short[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];

            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    ids[x][y] = (short)this.world.getIdForTile(this.getTileInner(layer, x, y));
                }
            }

            set.addShortShortArray("l_"+i, ids);

            set.addByteByteArray("m_"+i, this.metaGrid[i]);
        }

        for(int i = 0; i < this.lightGrid.length; i++){
            set.addByteByteArray("li_"+i, this.lightGrid[i]);
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
                tile.save(tileSet);

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
            updateSet.addInt("l_"+updateId, update.layer.ordinal());
            updateSet.addInt("t_"+updateId, update.time);
            updateSet.addInt("i_"+updateId, this.world.getIdForTile(update.tile));

            updateId++;
        }
        updateSet.addInt("a", updateId);

        set.addDataSet("s_u", updateSet);

        this.needsSave = false;
    }

    public void loadOrCreate(DataSet set){
        this.isGenerating = true;

        if(set != null && !set.isEmpty()){
            for(int i = 0; i < TileLayer.LAYERS.length; i++){
                TileLayer layer = TileLayer.LAYERS[i];
                short[][] ids = set.getShortShortArray("l_"+i, Constants.CHUNK_SIZE);
                byte[][] meta = set.getByteByteArray("m_"+i, Constants.CHUNK_SIZE);

                for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                    for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                        Tile tile = this.world.getTileForId(ids[x][y]);
                        if(tile != null){
                            this.setTileInner(layer, x, y, tile, meta[x][y]);
                        }
                        else{
                            Log.warn("Could not load tile at "+x+" "+y+" because id "+ids[x][y]+" is missing!");
                        }
                    }
                }
            }

            for(int i = 0; i < this.lightGrid.length; i++){
                this.lightGrid[i] = set.getByteByteArray("li_"+i, Constants.CHUNK_SIZE);
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
                    Log.error("Couldn't load entity with name "+name+" and data "+entitySet);
                }
            }

            int tileEntityAmount = set.getInt("t_a");
            for(int i = 0; i < tileEntityAmount; i++){
                DataSet tileSet = set.getDataSet("t_"+i);
                int x = tileSet.getInt("x");
                int y = tileSet.getInt("y");

                TileEntity tile = this.getTileEntity(x, y);
                if(tile != null){
                    tile.load(tileSet);
                }
                else{
                    Log.error("Couldn't load data of tile entity at "+x+", "+y+" because it is missing!");
                }
            }

            DataSet updateSet = set.getDataSet("s_u");

            int updateAmount = updateSet.getInt("a");
            for(int i = 0; i < updateAmount; i++){
                int x = updateSet.getInt("x_"+i);
                int y = updateSet.getInt("y_"+i);
                int time = updateSet.getInt("t_"+i);

                int id = updateSet.getInt("i_"+i);
                Tile tile = this.world.getTileForId(id);

                if(tile != null){
                    TileLayer layer = TileLayer.LAYERS[updateSet.getInt("l_"+i)];
                    this.scheduleUpdate(x, y, layer, time);
                }
                else{
                    Log.warn("Could not load scheduled update at "+x+" "+y+" with time "+time+" because tile with id "+id+" is missing!");
                }
            }
        }
        else{
            this.generate(this.world.generatorRandom);
            this.world.calcInitialSkylight(this.x, this.y, this.x+Constants.CHUNK_SIZE-1, this.y+Constants.CHUNK_SIZE-1);
        }

        this.isGenerating = false;
    }

    @Override
    public int getScheduledUpdateAmount(){
        return this.scheduledUpdates.size();
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
    public Map<AbstractEntityPlayer, MutableInt> getLeftPlayerTimers(){
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

    protected static class ScheduledUpdate{

        public final int x;
        public final int y;
        public final TileLayer layer;
        public final Tile tile;

        public int time;

        public ScheduledUpdate(int x, int y, TileLayer layer, Tile tile, int time){
            this.x = x;
            this.y = y;
            this.layer = layer;
            this.tile = tile;

            this.time = time;
        }
    }
}
