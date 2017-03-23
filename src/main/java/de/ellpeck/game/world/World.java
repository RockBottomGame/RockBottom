package de.ellpeck.game.world;

import de.ellpeck.game.Constants;
import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.util.MathUtil;
import de.ellpeck.game.util.Vec2;
import de.ellpeck.game.world.Chunk.TileLayer;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.tile.Tile;
import de.ellpeck.game.world.tile.entity.TileEntity;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.util.*;

public class World implements IWorld{

    public final Random rand = new Random();

    private long seed;
    public final Random generatorRandom = new Random();

    public final List<Chunk> loadedChunks = new ArrayList<>();
    private final Map<Vec2, Chunk> chunkLookup = new HashMap<>();

    public List<EntityPlayer> players = new ArrayList<>();

    private final File chunksDirectory;
    private final File playerDirectory;

    private final File dataFile;

    public long totalTimeInWorld;
    public int currentWorldTime;

    public World(File worldDirectory){
        this.chunksDirectory = new File(worldDirectory, "chunks");
        this.playerDirectory = new File(worldDirectory, "players");
        this.dataFile = new File(worldDirectory, "world_info.dat");

        this.load();
    }

    public void setSeed(long seed){
        this.seed = seed;
        this.generatorRandom.setSeed(seed);
    }

    public long getSeed(){
        return this.seed;
    }

    public void update(Game game){
        for(EntityPlayer player : this.players){
            for(int x = -Constants.CHUNK_LOAD_DISTANCE; x <= Constants.CHUNK_LOAD_DISTANCE; x++){
                for(int y = -Constants.CHUNK_LOAD_DISTANCE; y <= Constants.CHUNK_LOAD_DISTANCE; y++){
                    Chunk chunk = this.getChunkFromGridCoords(player.chunkX+x, player.chunkY+y);
                    chunk.loadTimer = 250;
                }
            }
        }

        for(int i = 0; i < this.loadedChunks.size(); i++){
            Chunk chunk = this.loadedChunks.get(i);
            chunk.update(game);

            chunk.loadTimer--;
            if(chunk.loadTimer <= 0 || chunk.shouldUnload()){
                this.saveChunk(chunk);

                this.loadedChunks.remove(i);
                this.chunkLookup.remove(new Vec2(chunk.gridX, chunk.gridY));
                i--;
            }
        }
    }

    @Override
    public void addEntity(Entity entity){
        Chunk chunk = this.getChunk(entity.x, entity.y);
        chunk.addEntity(entity);

        if(entity instanceof EntityPlayer){
            this.players.add((EntityPlayer)entity);
        }
    }

    @Override
    public void addTileEntity(TileEntity tile){
        Chunk chunk = this.getChunk(tile.x, tile.y);
        chunk.addTileEntity(tile);
    }

    @Override
    public void removeEntity(Entity entity){
        Chunk chunk = this.getChunk(entity.x, entity.y);
        chunk.removeEntity(entity);

        if(entity instanceof EntityPlayer){
            this.players.remove(entity);
        }
    }

    @Override
    public void removeTileEntity(int x, int y){
        Chunk chunk = this.getChunk(x, y);
        chunk.removeTileEntity(x, y);
    }

    @Override
    public TileEntity getTileEntity(int x, int y){
        Chunk chunk = this.getChunk(x, y);
        return chunk.getTileEntity(x, y);
    }

    @Override
    public List<Entity> getAllEntities(){
        List<Entity> entities = new ArrayList<>();
        for(Chunk chunk : this.loadedChunks){
            entities.addAll(chunk.getAllEntities());
        }
        return entities;
    }

    @Override
    public List<TileEntity> getAllTileEntities(){
        List<TileEntity> tiles = new ArrayList<>();
        for(Chunk chunk : this.loadedChunks){
            tiles.addAll(chunk.getAllTileEntities());
        }
        return tiles;
    }

    @Override
    public List<Entity> getEntities(BoundBox area){
        int minChunkX = MathUtil.toGridPos(area.getMinX())-1;
        int minChunkY = MathUtil.toGridPos(area.getMinY())-1;
        int maxChunkX = MathUtil.toGridPos(area.getMaxX())+1;
        int maxChunkY = MathUtil.toGridPos(area.getMaxY())+1;

        List<Entity> entities = new ArrayList<>();
        for(int x = minChunkX; x <= maxChunkX; x++){
            for(int y = minChunkY; y <= maxChunkY; y++){
                Chunk chunk = this.getChunkFromGridCoords(x, y);
                entities.addAll(chunk.getEntities(area));
            }
        }
        return entities;
    }

    @Override
    public List<BoundBox> getCollisions(BoundBox area){
        List<BoundBox> collisions = new ArrayList<>();

        for(int x = MathUtil.floor(area.getMinX()); x <= MathUtil.ceil(area.getMaxX()); x++){
            for(int y = MathUtil.floor(area.getMinY()); y <= MathUtil.ceil(area.getMaxY()); y++){
                Tile tile = this.getTile(x, y);

                BoundBox box = tile.getBoundBox(this, x, y);
                if(box != null && !box.isEmpty()){
                    collisions.add(box.copy().add(x, y));
                }
            }
        }

        return collisions;
    }

    public Chunk getChunk(double x, double y){
        return this.getChunkFromGridCoords(MathUtil.toGridPos(x), MathUtil.toGridPos(y));
    }

    public Chunk getChunkFromGridCoords(int gridX, int gridY){
        Chunk chunk = this.chunkLookup.get(new Vec2(gridX, gridY));

        if(chunk == null){
            DataSet set = new DataSet();
            set.read(new File(this.chunksDirectory, "c_"+gridX+"_"+gridY+".dat"));

            chunk = new Chunk(this, gridX, gridY, set);

            this.loadedChunks.add(chunk);
            this.chunkLookup.put(new Vec2(gridX, gridY), chunk);
        }

        return chunk;
    }

    @Override
    public Tile getTile(int x, int y){
        return this.getTile(TileLayer.MAIN, x, y);
    }

    @Override
    public Tile getTile(TileLayer layer, int x, int y){
        Chunk chunk = this.getChunk(x, y);
        return chunk.getTile(layer, x, y);
    }

    @Override
    public byte getMeta(int x, int y){
        return this.getMeta(TileLayer.MAIN, x, y);
    }

    @Override
    public byte getMeta(TileLayer layer, int x, int y){
        Chunk chunk = this.getChunk(x, y);
        return chunk.getMeta(layer, x, y);
    }

    @Override
    public void setTile(int x, int y, Tile tile){
        this.setTile(TileLayer.MAIN, x, y, tile);
    }

    @Override
    public void setTile(TileLayer layer, int x, int y, Tile tile){
        Chunk chunk = this.getChunk(x, y);
        chunk.setTile(layer, x, y, tile);
    }

    @Override
    public void setMeta(int x, int y, byte meta){
        this.setMeta(TileLayer.MAIN, x, y, meta);
    }

    @Override
    public void setMeta(TileLayer layer, int x, int y, byte meta){
        Chunk chunk = this.getChunk(x, y);
        chunk.setMeta(layer, x, y, meta);
    }

    public void notifyNeighborsOfChange(int x, int y){
        for(Direction direction : Direction.ADJACENT_DIRECTIONS){
            int offX = x+direction.x;
            int offY = y+direction.y;

            Tile tile = this.getTile(offX, offY);
            tile.onChangeAround(this, offX, offY, x, y);
        }
    }

    public void save(){
        for(Chunk chunk : this.loadedChunks){
            this.saveChunk(chunk);
        }

        DataSet dataSet = new DataSet();
        dataSet.addLong("seed", this.seed);
        dataSet.addLong("total_time", this.totalTimeInWorld);
        dataSet.addInt("curr_time", this.currentWorldTime);

        dataSet.write(this.dataFile);

        for(EntityPlayer player : this.players){
            DataSet playerSet = new DataSet();
            player.save(playerSet);

            playerSet.write(new File(this.playerDirectory, player.getUniqueId().toString()+".dat"));
        }
    }

    public void load(){
        DataSet dataSet = new DataSet();
        dataSet.read(this.dataFile);

        this.setSeed(dataSet.getLong("seed"));
        this.totalTimeInWorld = dataSet.getLong("total_time");
        this.currentWorldTime = dataSet.getInt("curr_time");
    }

    public EntityPlayer addPlayer(UUID id){
        EntityPlayer player = new EntityPlayer(this, id);

        File file = new File(this.playerDirectory, id+".dat");
        if(file.exists()){
            DataSet set = new DataSet();
            set.read(file);

            player.load(set);
            Log.info("Loading player with unique id "+id+"!");
        }
        else{
            player.setPos(0, 10);
            Log.info("Adding new player with unique id "+id+" to world!");
        }

        this.addEntity(player);

        return player;
    }

    private void saveChunk(Chunk chunk){
        if(chunk.needsSave()){
            DataSet set = new DataSet();
            chunk.save(set);

            set.write(new File(this.chunksDirectory, "c_"+chunk.gridX+"_"+chunk.gridY+".dat"));
        }
    }

    public void destroyTile(int x, int y, TileLayer layer, Entity destroyer){
        Tile tile = this.getTile(layer, x, y);
        byte meta = this.getMeta(x, y);

        tile.onDestroyed(this, x, y, destroyer, layer);

        Game.get().particleManager.addTileParticles(this, x, y, tile, meta);

        this.setTile(layer, x, y, ContentRegistry.TILE_AIR);
    }
}
