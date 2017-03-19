package de.ellpeck.game.world;

import de.ellpeck.game.Constants;
import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.MathUtil;
import de.ellpeck.game.util.Vec2;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.tile.Tile;
import de.ellpeck.game.world.tile.entity.TileEntity;

import java.util.*;
import java.util.stream.Collectors;

public class Chunk implements IWorld{

    public final int x;
    public final int y;

    private final World world;

    private final Tile[][][] tileGrid = new Tile[TileLayer.values().length][Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    private final byte[][] metaGrid = new byte[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];

    private final List<Entity> entities = new ArrayList<>();

    private final List<TileEntity> tileEntities = new ArrayList<>();
    private final Map<Vec2, TileEntity> tileEntityLookup = new HashMap<>();

    public int loadTimer;

    public Chunk(World world, int gridX, int gridY){
        this.world = world;

        this.x = MathUtil.toWorldPos(gridX);
        this.y = MathUtil.toWorldPos(gridY);

        for(int layer = 0; layer < this.tileGrid.length; layer++){
            for(int row = 0; row < this.tileGrid[layer].length; row++){
                for(int column = 0; column < this.tileGrid[layer][row].length; column++){
                    this.tileGrid[layer][row][column] = ContentRegistry.TILE_AIR;
                }
            }
        }
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
                int currChunkX = MathUtil.toGridPos(entity.x);
                int currChunkY = MathUtil.toGridPos(entity.y);

                if(currChunkX != entity.chunkX || currChunkY != entity.chunkY){
                    Chunk chunk = this.world.getChunkFromGridCoords(currChunkX, currChunkY);
                    chunk.addEntity(entity);

                    entity.chunkX = currChunkX;
                    entity.chunkY = currChunkY;

                    this.removeEntity(entity);
                    i--;
                }
            }
        }

        for(int i = 0; i < this.tileEntities.size(); i++){
            TileEntity tile = this.tileEntities.get(i);
            tile.update(game);

            if(tile.shouldRemove()){
                this.world.removeTileEntity(tile.x, tile.y);
                i--;
            }
        }
    }

    public int getGridX(){
        return MathUtil.toGridPos(this.x);
    }

    public int getGridY(){
        return MathUtil.toGridPos(this.y);
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
        return this.getMetaInner(x-this.x, y-this.y);
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
        this.setMeta(x, y, (byte)meta);
    }

    @Override
    public void setMeta(int x, int y, byte meta){
        this.setMetaInner(x-this.x, y-this.y, meta);
    }

    public Tile getTileInner(TileLayer layer, int x, int y){
        return this.tileGrid[layer.ordinal()][x][y];
    }

    public Tile getTileInner(int x, int y){
        return this.getTileInner(TileLayer.MAIN, x, y);
    }

    public byte getMetaInner(int x, int y){
        return this.metaGrid[x][y];
    }

    public void setTileInner(int x, int y, Tile tile){
        this.setTileInner(TileLayer.MAIN, x, y, tile);
    }

    public void setTileInner(TileLayer layer, int x, int y, Tile tile){
        Tile lastTile = this.getTileInner(x, y);
        lastTile.onRemoved(this.world, this.x+x, this.y+y);

        if(lastTile.providesTileEntity()){
            this.removeTileEntity(this.x+x, this.y+y);
        }

        this.tileGrid[layer.ordinal()][x][y] = tile;
        tile.onAdded(this.world, this.x+x, this.y+y);

        if(tile.providesTileEntity()){
            TileEntity tileEntity = tile.provideTileEntity(this.world, this.x+x, this.y+y);
            if(tileEntity != null){
                this.addTileEntity(tileEntity);
            }
        }

        this.world.notifyNeighborsOfChange(x, y);
    }

    public void setMetaInner(int x, int y, byte meta){
        this.metaGrid[x][y] = meta;

        this.world.notifyNeighborsOfChange(x, y);
    }

    @Override
    public void addEntity(Entity entity){
        this.entities.add(entity);
    }

    @Override
    public void addTileEntity(TileEntity tile){
        this.tileEntities.add(tile);
        this.tileEntityLookup.put(new Vec2(tile.x, tile.y), tile);

        this.world.notifyNeighborsOfChange(tile.x, tile.y);
    }

    @Override
    public void removeEntity(Entity entity){
        this.entities.remove(entity);
    }

    @Override
    public void removeTileEntity(int x, int y){
        TileEntity tile = this.world.getTileEntity(x, y);
        if(tile != null){
            this.tileEntities.remove(tile);
            this.tileEntityLookup.remove(new Vec2(tile.x, tile.y));

            this.world.notifyNeighborsOfChange(x, y);
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

    public enum TileLayer{
        MAIN,
        BACKGROUND
    }
}
