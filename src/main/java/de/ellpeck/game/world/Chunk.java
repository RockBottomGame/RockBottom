package de.ellpeck.game.world;

import de.ellpeck.game.Constants;
import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.MathUtil;
import de.ellpeck.game.util.Vec2;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.tile.Tile;
import de.ellpeck.game.world.tile.entity.TileEntity;
import org.newdawn.slick.util.Log;

import java.util.*;
import java.util.function.Predicate;

public class Chunk implements IWorld{

    public final int x;
    public final int y;

    public final int gridX;
    public final int gridY;

    private final World world;

    private final Tile[][][] tileGrid = new Tile[TileLayer.LAYERS.length][Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    private final byte[][][] metaGrid = new byte[TileLayer.LAYERS.length][Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    private final byte[][][] lightGrid = new byte[2][Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];

    private final List<Entity> entities = new ArrayList<>();

    private final List<TileEntity> tileEntities = new ArrayList<>();
    private final Map<Vec2, TileEntity> tileEntityLookup = new HashMap<>();

    private final List<ScheduledUpdate> scheduledUpdates = new ArrayList<>();
    private final Map<Vec2, ScheduledUpdate> scheduledUpdateLookup = new HashMap<>();

    public int randomUpdateTileAmount;

    public int loadTimer;

    private boolean isDirty;
    private boolean isGenerating;

    public Chunk(World world, int gridX, int gridY){
        this.world = world;

        this.x = MathUtil.toWorldPos(gridX);
        this.y = MathUtil.toWorldPos(gridY);
        this.gridX = gridX;
        this.gridY = gridY;

        for(int i = 0; i < TileLayer.LAYERS.length; i++){
            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    this.tileGrid[i][x][y] = ContentRegistry.TILE_AIR;
                }
            }
        }
    }

    public void generate(Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                if(this.y+y == 15){
                    this.setTileInner(x, y, ContentRegistry.TILE_GRASS);
                }
                else if(this.y+y < 15){
                    this.setTileInner(x, y, rand.nextFloat() <= 0.75 ? ContentRegistry.TILE_DIRT : ContentRegistry.TILE_ROCK);

                    this.setTileInner(TileLayer.BACKGROUND, x, y, rand.nextFloat() <= 0.75 ? ContentRegistry.TILE_DIRT : ContentRegistry.TILE_ROCK);
                }
            }
        }
    }

    public void update(Game game){
        if(this.tileEntities.size() != this.tileEntityLookup.size()){
            throw new RuntimeException("TileEntities and TileEntityLookup are out of sync!");
        }
        if(this.scheduledUpdates.size() != this.scheduledUpdateLookup.size()){
            throw new RuntimeException("ScheduledUpdates and ScheduledUpdateLookup are out of sync!");
        }

        for(int i = 0; i < this.entities.size(); i++){
            Entity entity = this.entities.get(i);
            entity.update(game);

            if(entity.isDead()){
                this.world.removeEntity(entity);
                i--;
            }
            else{
                if(entity.isDirty()){
                    this.isDirty = true;
                }

                int newChunkX = MathUtil.toGridPos(entity.x);
                int newChunkY = MathUtil.toGridPos(entity.y);

                if(newChunkX != this.gridX || newChunkY != this.gridY){
                    this.removeEntity(entity);
                    i--;

                    Chunk chunk = this.world.getChunkFromGridCoords(newChunkX, newChunkY);
                    chunk.addEntity(entity);
                }
            }
        }

        for(int i = 0; i < this.tileEntities.size(); i++){
            TileEntity tile = this.tileEntities.get(i);
            tile.update(game);

            if(tile.shouldRemove()){
                this.removeTileEntity(tile.x, tile.y);
                i--;
            }
            else if(tile.isDirty()){
                this.isDirty = true;
            }
        }

        if(this.randomUpdateTileAmount > 0){
            int randX = this.world.rand.nextInt(Constants.CHUNK_SIZE);
            int randY = this.world.rand.nextInt(Constants.CHUNK_SIZE);

            Tile tile = this.getTileInner(randX, randY);
            if(tile.doesRandomUpdates()){
                tile.updateRandomly(this.world, this.x+randX, this.y+randY);
            }
        }

        if(!this.scheduledUpdates.isEmpty()){
            for(int i = 0; i < this.scheduledUpdates.size(); i++){
                ScheduledUpdate update = this.scheduledUpdates.get(i);
                update.time--;

                if(update.time <= 0){
                    this.scheduledUpdates.remove(i);
                    this.scheduledUpdateLookup.remove(new Vec2(update.x, update.y));

                    Tile tile = this.getTile(update.x, update.y);
                    if(tile == update.tile){
                        tile.onScheduledUpdate(this.world, update.x, update.y, update.layer);
                    }

                    i--;
                    this.isDirty = true;
                }
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
        this.setTileInner(layer, x-this.x, y-this.y, tile);
    }

    @Override
    public void setMeta(int x, int y, int meta){
        this.setMeta(TileLayer.MAIN, x, y, meta);
    }

    @Override
    public void setMeta(TileLayer layer, int x, int y, int meta){
        this.setMetaInner(layer, x-this.x, y-this.y, meta);
    }

    public Tile getTileInner(TileLayer layer, int x, int y){
        return this.tileGrid[layer.ordinal()][x][y];
    }

    public Tile getTileInner(int x, int y){
        return this.getTileInner(TileLayer.MAIN, x, y);
    }

    public byte getMetaInner(TileLayer layer, int x, int y){
        return this.metaGrid[layer.ordinal()][x][y];
    }

    public void setTileInner(int x, int y, Tile tile){
        this.setTileInner(TileLayer.MAIN, x, y, tile);
    }

    public void setTileInner(TileLayer layer, int x, int y, Tile tile){
        Tile lastTile = this.getTileInner(layer, x, y);

        boolean lastAir = lastTile.isAir();
        byte lastLight = lastTile.getLight(this.world, this.x+x, this.y+y, layer);
        float lastMofifier = lastTile.getTranslucentModifier(this.world, this.x+x, this.y+y, layer);

        lastTile.onRemoved(this.world, this.x+x, this.y+y);

        if(layer == TileLayer.MAIN){
            if(lastTile.providesTileEntity()){
                this.removeTileEntity(this.x+x, this.y+y);
            }

            if(lastTile.doesRandomUpdates()){
                this.randomUpdateTileAmount--;
            }
        }

        int ord = layer.ordinal();
        this.tileGrid[ord][x][y] = tile;
        this.metaGrid[ord][x][y] = 0;

        tile.onAdded(this.world, this.x+x, this.y+y);

        if(layer == TileLayer.MAIN){
            if(tile.providesTileEntity()){
                TileEntity tileEntity = tile.provideTileEntity(this.world, this.x+x, this.y+y);
                if(tileEntity != null){
                    this.addTileEntity(tileEntity);
                }
            }

            if(tile.doesRandomUpdates()){
                this.randomUpdateTileAmount++;
            }
        }

        if(!this.isGenerating){
            if(lastAir != tile.isAir() || lastLight != tile.getLight(this.world, this.x+x, this.y+y, layer) || lastMofifier != tile.getTranslucentModifier(this.world, this.x+x, this.y+y, layer)){
                this.world.updateLightFrom(this.x+x, this.y+y);
            }

            this.world.notifyNeighborsOfChange(this.x+x, this.y+y, layer);
            this.isDirty = true;
        }
    }

    public void setMetaInner(TileLayer layer, int x, int y, int meta){
        if(meta > Byte.MAX_VALUE){
            throw new IndexOutOfBoundsException("Tried assigning meta "+meta+" in chunk at "+this.gridX+", "+this.gridY+" which is greater than max "+Byte.MAX_VALUE+"!");
        }

        this.metaGrid[layer.ordinal()][x][y] = (byte)meta;

        if(!this.isGenerating){
            this.world.notifyNeighborsOfChange(this.x+x, this.y+y, layer);
            this.isDirty = true;
        }
    }

    @Override
    public void addEntity(Entity entity){
        this.entities.add(entity);

        entity.chunkX = this.gridX;
        entity.chunkY = this.gridY;

        if(!this.isGenerating){
            this.isDirty = true;
        }
    }

    @Override
    public void addTileEntity(TileEntity tile){
        Vec2 posVec = new Vec2(tile.x, tile.y);
        if(!this.tileEntityLookup.containsKey(posVec)){
            this.tileEntities.add(tile);
            this.tileEntityLookup.put(posVec, tile);

            if(!this.isGenerating){
                this.world.notifyNeighborsOfChange(tile.x, tile.y, TileLayer.MAIN);
                this.isDirty = true;
            }
        }
    }

    @Override
    public void removeEntity(Entity entity){
        this.entities.remove(entity);

        if(!this.isGenerating){
            this.isDirty = true;
        }
    }

    @Override
    public void removeTileEntity(int x, int y){
        TileEntity tile = this.getTileEntity(x, y);
        if(tile != null){
            this.tileEntities.remove(tile);
            this.tileEntityLookup.remove(new Vec2(tile.x, tile.y));

            if(!this.isGenerating){
                this.world.notifyNeighborsOfChange(this.x+x, this.y+y, TileLayer.MAIN);
                this.isDirty = true;
            }
        }
    }

    @Override
    public TileEntity getTileEntity(int x, int y){
        return this.tileEntityLookup.get(new Vec2(x, y));
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
    public List<BoundBox> getCollisions(BoundBox area){
        return this.world.getCollisions(area);
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
    public boolean isLoaded(int x, int y){
        return true;
    }

    @Override
    public void scheduleUpdate(int x, int y, TileLayer layer, int time){
        Vec2 posVec = new Vec2(x, y);
        if(!this.scheduledUpdateLookup.containsKey(posVec)){
            ScheduledUpdate update = new ScheduledUpdate(x, y, layer, this.getTile(x, y), time);

            this.scheduledUpdateLookup.put(posVec, update);
            this.scheduledUpdates.add(update);

            if(!this.isGenerating){
                this.isDirty = true;
            }
        }
    }

    public byte getCombinedLightInner(int x, int y){
        byte artificial = this.getArtificialLightInner(x, y);
        byte sky = (byte)(this.getSkylightInner(x, y)*this.world.getSkylightModifier());

        return (byte)Math.min(Constants.MAX_LIGHT, artificial+sky);
    }

    public byte getSkylightInner(int x, int y){
        return this.lightGrid[0][x][y];
    }

    public void setSkylightInner(int x, int y, byte light){
        this.lightGrid[0][x][y] = light;
    }

    public byte getArtificialLightInner(int x, int y){
        return this.lightGrid[1][x][y];
    }

    public void setArtificialLightInner(int x, int y, byte light){
        this.lightGrid[1][x][y] = light;
    }

    public boolean shouldUnload(){
        return this.loadTimer <= 0;
    }

    public boolean needsSave(){
        return this.isDirty;
    }

    public void save(DataSet set){
        for(int i = 0; i < TileLayer.LAYERS.length; i++){
            TileLayer layer = TileLayer.LAYERS[i];
            short[][] ids = new short[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];

            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    ids[x][y] = (short)ContentRegistry.TILE_REGISTRY.getId(this.getTileInner(layer, x, y));
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
            if(!(entity instanceof EntityPlayer)){
                DataSet entitySet = new DataSet();
                entitySet.addInt("id", ContentRegistry.ENTITY_REGISTRY.getId(entity.getClass()));
                entity.save(entitySet);

                set.addDataSet("e_"+entityId, entitySet);

                entityId++;
            }
        }
        set.addInt("e_a", entityId);

        int tileEntityId = 0;
        for(TileEntity tile : this.tileEntities){
            DataSet tileSet = new DataSet();
            tileSet.addInt("x", tile.x);
            tileSet.addInt("y", tile.y);
            tile.save(tileSet);

            set.addDataSet("t_"+tileEntityId, tileSet);

            tileEntityId++;
        }
        set.addInt("t_a", tileEntityId);

        DataSet updateSet = new DataSet();

        int updateId = 0;
        for(ScheduledUpdate update : this.scheduledUpdates){
            updateSet.addInt("x_"+updateId, update.x);
            updateSet.addInt("y_"+updateId, update.y);
            updateSet.addInt("l_"+updateId, update.layer.ordinal());
            updateSet.addInt("t_"+updateId, update.time);
            updateSet.addInt("i_"+updateId, ContentRegistry.TILE_REGISTRY.getId(update.tile));

            updateId++;
        }
        updateSet.addInt("a", updateId);

        set.addDataSet("s_u", updateSet);

        this.isDirty = false;
    }

    public void loadOrCreate(DataSet set){
        this.isGenerating = true;

        if(set != null && !set.isEmpty()){
            for(int i = 0; i < TileLayer.LAYERS.length; i++){
                TileLayer layer = TileLayer.LAYERS[i];
                short[][] ids = set.getShortShortArray("l_"+i, Constants.CHUNK_SIZE);

                for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                    for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                        Tile tile = ContentRegistry.TILE_REGISTRY.get(ids[x][y]);
                        if(tile != null){
                            this.setTileInner(layer, x, y, tile);
                        }
                        else{
                            Log.warn("Could not load tile at "+x+" "+y+" because id "+ids[x][y]+" is missing!");
                        }
                    }
                }

                this.metaGrid[i] = set.getByteByteArray("m_"+i, Constants.CHUNK_SIZE);
            }

            for(int i = 0; i < this.lightGrid.length; i++){
                this.lightGrid[i] = set.getByteByteArray("li_"+i, Constants.CHUNK_SIZE);
            }

            int entityAmount = set.getInt("e_a");
            for(int i = 0; i < entityAmount; i++){
                DataSet entitySet = set.getDataSet("e_"+i);

                int id = entitySet.getInt("id");
                Class<? extends Entity> entityClass = ContentRegistry.ENTITY_REGISTRY.get(id);

                try{
                    Entity entity = entityClass.getConstructor(World.class).newInstance(this.world);
                    entity.load(entitySet);
                    this.addEntity(entity);
                }
                catch(Exception e){
                    Log.error("Couldn't load entity with id "+id+" and data "+entitySet+"!", e);
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
                Tile tile = ContentRegistry.TILE_REGISTRY.get(id);

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

    public int getScheduledUpdateAmount(){
        return this.scheduledUpdates.size();
    }

    private static class ScheduledUpdate{

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
