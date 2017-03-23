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

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Chunk implements IWorld{

    public final int x;
    public final int y;

    public final int gridX;
    public final int gridY;

    private final World world;

    private final Tile[][][] tileGrid = new Tile[TileLayer.LAYERS.length][Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    private final byte[][][] metaGrid = new byte[TileLayer.LAYERS.length][Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];

    private final List<Entity> entities = new ArrayList<>();

    private final List<TileEntity> tileEntities = new ArrayList<>();
    private final Map<Vec2, TileEntity> tileEntityLookup = new HashMap<>();

    public int randomUpdateTileAmount;

    public int loadTimer;

    private boolean isDirty;
    private boolean isGenerating;

    public Chunk(World world, int gridX, int gridY, DataSet set){
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

        this.loadOrCreate(set);
    }

    public void generate(Random rand){
        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                if(this.y+y == 0){
                    this.setTileInner(x, y, ContentRegistry.TILE_GRASS);
                }
                else if(this.y+y < 0){
                    this.setTileInner(x, y, rand.nextFloat() <= 0.75 ? ContentRegistry.TILE_DIRT : ContentRegistry.TILE_ROCK);

                    this.setTileInner(TileLayer.BACKGROUND, x, y, rand.nextFloat() <= 0.75 ? ContentRegistry.TILE_DIRT : ContentRegistry.TILE_ROCK);
                }
            }
        }
    }

    public void update(Game game){
        for(int i = 0; i < this.entities.size(); i++){
            Entity entity = this.entities.get(i);
            entity.update(game);

            if(entity.isDead()){
                this.world.removeEntity(entity);
                i--;
            }
            else{
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
        }

        if(this.randomUpdateTileAmount > 0){
            int randX = this.world.rand.nextInt(Constants.CHUNK_SIZE);
            int randY = this.world.rand.nextInt(Constants.CHUNK_SIZE);

            Tile tile = this.getTileInner(randX, randY);
            if(tile.doesRandomUpdates()){
                tile.updateRandomly(this.world, this.x+randX, this.y+randY);
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
    public byte getMeta(int x, int y){
        return this.getMeta(TileLayer.MAIN, x, y);
    }

    @Override
    public byte getMeta(TileLayer layer, int x, int y){
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
    public void setMeta(int x, int y, byte meta){
        this.setMeta(TileLayer.MAIN, x, y, meta);
    }

    @Override
    public void setMeta(TileLayer layer, int x, int y, byte meta){
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
        Tile lastTile = this.getTileInner(x, y);
        lastTile.onRemoved(this.world, this.x+x, this.y+y);

        if(layer == TileLayer.MAIN){
            if(lastTile.providesTileEntity()){
                this.removeTileEntity(this.x+x, this.y+y);
            }

            if(lastTile.doesRandomUpdates()){
                this.randomUpdateTileAmount--;
            }
        }

        this.tileGrid[layer.ordinal()][x][y] = tile;
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
            if(layer == TileLayer.MAIN){
                this.world.notifyNeighborsOfChange(this.x+x, this.y+y);
            }

            this.isDirty = true;
        }
    }

    public void setMetaInner(TileLayer layer, int x, int y, byte meta){
        this.metaGrid[layer.ordinal()][x][y] = meta;

        if(!this.isGenerating){
            this.world.notifyNeighborsOfChange(this.x+x, this.y+y);
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
        this.tileEntities.add(tile);
        this.tileEntityLookup.put(new Vec2(tile.x, tile.y), tile);

        if(!this.isGenerating){
            this.world.notifyNeighborsOfChange(tile.x, tile.y);
            this.isDirty = true;
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
                this.world.notifyNeighborsOfChange(this.x+x, this.y+y);
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
        return this.entities.stream().filter(entity -> !entity.isDead() && entity.getBoundingBox().copy().add(entity.x, entity.y).intersects(area)).collect(Collectors.toList());
    }

    @Override
    public List<BoundBox> getCollisions(BoundBox area){
        return this.world.getCollisions(area);
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
            int[][] ids = new int[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];

            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    ids[x][y] = ContentRegistry.TILE_REGISTRY.getId(this.getTileInner(layer, x, y));
                }
            }

            set.addIntIntArray("l_"+i, ids);

            set.addByteByteArray("m_"+i, this.metaGrid[i]);
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

        this.isDirty = false;
    }

    public void loadOrCreate(DataSet set){
        this.isGenerating = true;

        if(set != null && !set.isEmpty()){
            for(int i = 0; i < TileLayer.LAYERS.length; i++){
                TileLayer layer = TileLayer.LAYERS[i];
                int[][] ids = set.getIntIntArray("l_"+i, Constants.CHUNK_SIZE);

                for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                    for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                        this.setTileInner(layer, x, y, ContentRegistry.TILE_REGISTRY.get(ids[x][y]));
                    }
                }

                this.metaGrid[i] = set.getByteByteArray("m_"+i, Constants.CHUNK_SIZE);
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
                catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
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
        }
        else{
            this.generate(this.world.generatorRandom);
        }

        this.isGenerating = false;
    }

    public enum TileLayer{
        MAIN,
        BACKGROUND;

        public static final TileLayer[] LAYERS = values();

        public TileLayer getOpposite(){
            return this == MAIN ? BACKGROUND : MAIN;
        }
    }
}
