package de.ellpeck.game.world;

import de.ellpeck.game.Constants;
import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.packet.toclient.PacketEntityChange;
import de.ellpeck.game.net.packet.toclient.PacketParticles;
import de.ellpeck.game.net.server.ConnectedPlayer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.util.Util;
import de.ellpeck.game.util.Vec2;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.tile.Tile;
import de.ellpeck.game.world.tile.entity.TileEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

public class World implements IWorld{

    public final Random rand = new Random();

    public final Random generatorRandom = new Random();

    public final List<Chunk> loadedChunks = new ArrayList<>();
    protected final Map<Vec2, Chunk> chunkLookup = new HashMap<>();

    public List<EntityPlayer> players = new ArrayList<>();

    protected File chunksDirectory;
    protected File playerDirectory;
    protected int saveTicksCounter;

    public int spawnX = 0;
    public int spawnY = 20;

    public WorldInfo info;

    public World(WorldInfo info){
        this.info = info;
        this.generatorRandom.setSeed(this.info.seed);
    }

    public void initFiles(File worldDirectory){
        this.chunksDirectory = new File(worldDirectory, "chunks");
        this.playerDirectory = new File(worldDirectory, "players");
    }

    protected void checkListSync(){
        if(this.loadedChunks.size() != this.chunkLookup.size()){
            throw new RuntimeException("LoadedChunks and ChunkLookup are out of sync!");
        }
    }

    public void update(Game game){
        this.checkListSync();

        for(int i = 0; i < this.loadedChunks.size(); i++){
            Chunk chunk = this.loadedChunks.get(i);
            chunk.update(game);

            if(chunk.shouldUnload()){
                this.saveChunk(chunk);

                this.loadedChunks.remove(i);
                this.chunkLookup.remove(new Vec2(chunk.gridX, chunk.gridY));
                i--;
            }
        }

        this.info.totalTimeInWorld++;

        this.info.currentWorldTime++;
        if(this.info.currentWorldTime >= Constants.TIME_PER_DAY){
            this.info.currentWorldTime = 0;
        }

        this.saveTicksCounter++;
        if(this.saveTicksCounter >= game.settings.autosaveIntervalSeconds*Constants.TARGET_TPS){
            this.saveTicksCounter = 0;

            this.save();
        }
    }

    @Override
    public void addEntity(Entity entity){
        Chunk chunk = this.getChunk(entity.x, entity.y);
        chunk.addEntity(entity);

        if(entity instanceof EntityPlayer){
            this.players.add((EntityPlayer)entity);
        }

        if(NetHandler.isServer()){
            NetHandler.sendToAllPlayersExcept(this, new PacketEntityChange(entity, false), entity);
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

        entity.onRemoveFromWorld();

        if(NetHandler.isServer()){
            NetHandler.sendToAllPlayersExcept(this, new PacketEntityChange(entity, true), entity);
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
    public Entity getEntity(UUID id){
        for(Chunk chunk : this.loadedChunks){
            Entity entity = chunk.getEntity(id);
            if(entity != null){
                return entity;
            }
        }
        return null;
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
        int minChunkX = Util.toGridPos(area.getMinX())-1;
        int minChunkY = Util.toGridPos(area.getMinY())-1;
        int maxChunkX = Util.toGridPos(area.getMaxX())+1;
        int maxChunkY = Util.toGridPos(area.getMaxY())+1;

        List<T> entities = new ArrayList<>();
        for(int x = minChunkX; x <= maxChunkX; x++){
            for(int y = minChunkY; y <= maxChunkY; y++){
                Chunk chunk = this.getChunkFromGridCoords(x, y);
                entities.addAll(chunk.getEntities(area, type, test));
            }
        }
        return entities;
    }

    @Override
    public List<BoundBox> getCollisions(BoundBox area){
        List<BoundBox> collisions = new ArrayList<>();

        for(int x = Util.floor(area.getMinX()); x <= Util.ceil(area.getMaxX()); x++){
            for(int y = Util.floor(area.getMinY()); y <= Util.ceil(area.getMaxY()); y++){
                Tile tile = this.getTile(x, y);

                BoundBox box = tile.getBoundBox(this, x, y);
                if(box != null && !box.isEmpty()){
                    collisions.add(box.copy().add(x, y));
                }
            }
        }

        return collisions;
    }

    @Override
    public byte getCombinedLight(int x, int y){
        Chunk chunk = this.getChunk(x, y);
        return chunk.getCombinedLight(x, y);
    }

    @Override
    public byte getSkyLight(int x, int y){
        Chunk chunk = this.getChunk(x, y);
        return chunk.getSkyLight(x, y);
    }

    @Override
    public byte getArtificialLight(int x, int y){
        Chunk chunk = this.getChunk(x, y);
        return chunk.getArtificialLight(x, y);
    }

    @Override
    public void setSkyLight(int x, int y, byte light){
        Chunk chunk = this.getChunk(x, y);
        chunk.setSkyLight(x, y, light);
    }

    @Override
    public void setArtificialLight(int x, int y, byte light){
        Chunk chunk = this.getChunk(x, y);
        chunk.setArtificialLight(x, y, light);
    }

    @Override
    public boolean isChunkLoaded(int x, int y){
        return this.chunkLookup.containsKey(new Vec2(x, y));
    }

    @Override
    public boolean isPosLoaded(int x, int y){
        return this.isChunkLoaded(Util.toGridPos(x), Util.toGridPos(y));
    }

    @Override
    public void scheduleUpdate(int x, int y, TileLayer layer, int time){
        Chunk chunk = this.getChunk(x, y);
        chunk.scheduleUpdate(x, y, layer, time);
    }

    @Override
    public void setDirty(int x, int y){
        Chunk chunk = this.getChunk(x, y);
        chunk.setDirty(x, y);
    }

    public Chunk getChunk(double x, double y){
        return this.getChunkFromGridCoords(Util.toGridPos(x), Util.toGridPos(y));
    }

    public Chunk getChunkFromGridCoords(int gridX, int gridY){
        Chunk chunk = this.chunkLookup.get(new Vec2(gridX, gridY));

        if(chunk == null){
            chunk = this.loadChunk(gridX, gridY);
        }

        return chunk;
    }

    protected Chunk loadChunk(int gridX, int gridY){
        Chunk chunk = new Chunk(this, gridX, gridY);
        this.loadedChunks.add(chunk);
        this.chunkLookup.put(new Vec2(gridX, gridY), chunk);

        DataSet set = new DataSet();
        set.read(new File(this.chunksDirectory, "c_"+gridX+"_"+gridY+".dat"));
        chunk.loadOrCreate(set);

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
    public int getMeta(int x, int y){
        return this.getMeta(TileLayer.MAIN, x, y);
    }

    @Override
    public int getMeta(TileLayer layer, int x, int y){
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
    public void setMeta(int x, int y, int meta){
        this.setMeta(TileLayer.MAIN, x, y, meta);
    }

    @Override
    public void setMeta(TileLayer layer, int x, int y, int meta){
        Chunk chunk = this.getChunk(x, y);
        chunk.setMeta(layer, x, y, meta);
    }

    public void notifyNeighborsOfChange(int x, int y, TileLayer layer){
        for(Direction direction : Direction.ADJACENT_DIRECTIONS){
            int offX = x+direction.x;
            int offY = y+direction.y;

            this.getTile(layer, offX, offY).onChangeAround(this, offX, offY, layer, x, y, layer);
        }

        TileLayer opp = layer.getOpposite();
        this.getTile(opp, x, y).onChangeAround(this, x, y, opp, x, y, layer);
    }

    public void save(){
        long timeStarted = System.currentTimeMillis();
        Log.info("Saving world...");

        for(Chunk chunk : this.loadedChunks){
            this.saveChunk(chunk);
        }

        this.info.save();

        for(EntityPlayer player : this.players){
           this.savePlayer(player);
        }

        Log.info("Finished saving world, took "+(System.currentTimeMillis()-timeStarted)+"ms.");
    }

    public void savePlayer(EntityPlayer player){
        DataSet playerSet = new DataSet();
        player.save(playerSet);

        playerSet.write(new File(this.playerDirectory, player.getUniqueId().toString()+".dat"));
    }

    public EntityPlayer createPlayer(UUID id, Channel channel){
        EntityPlayer player = channel != null ? new ConnectedPlayer(this, id, channel) : new EntityPlayer(this, id);

        File file = new File(this.playerDirectory, id+".dat");
        if(file.exists()){
            DataSet set = new DataSet();
            set.read(file);

            player.load(set);
            Log.info("Loading player with unique id "+id+"!");
        }
        else{
            player.resetAndSpawn(Game.get());
            Log.info("Adding new player with unique id "+id+" to world!");
        }
        return player;
    }

    public EntityPlayer getPlayer(UUID id){
        for(EntityPlayer player : this.players){
            if(id.equals(player.getUniqueId())){
                return player;
            }
        }
        return null;
    }

    protected void saveChunk(Chunk chunk){
        if(chunk.needsSave){
            DataSet set = new DataSet();
            chunk.save(set);

            set.write(new File(this.chunksDirectory, "c_"+chunk.gridX+"_"+chunk.gridY+".dat"));
        }
    }

    public void destroyTile(int x, int y, TileLayer layer, Entity destroyer, boolean forceDrop){
        Tile tile = this.getTile(layer, x, y);
        int meta = this.getMeta(x, y);

        tile.onDestroyed(this, x, y, destroyer, layer, forceDrop);

        if(NetHandler.isServer()){
            NetHandler.sendToAllPlayers(this, PacketParticles.tile(x, y, tile, meta));
        }

        Game.get().particleManager.addTileParticles(this, x, y, tile, meta);

        this.setTile(layer, x, y, ContentRegistry.TILE_AIR);
    }

    public void updateLightFrom(int x, int y){
        for(Direction direction : Direction.ALL_DIRECTIONS){
            int dirX = x+direction.x;
            int dirY = y+direction.y;

            if(this.isPosLoaded(dirX, dirY)){
                boolean change = false;

                byte skylightThere = this.getSkyLight(dirX, dirY);
                byte calcedSkylight = this.calcLight(dirX, dirY, true);
                if(calcedSkylight != skylightThere){
                    this.setSkyLight(dirX, dirY, calcedSkylight);
                    change = true;
                }

                byte artLightThere = this.getArtificialLight(dirX, dirY);
                byte calcedArtLight = this.calcLight(dirX, dirY, false);
                if(calcedArtLight != artLightThere){
                    this.setArtificialLight(dirX, dirY, calcedArtLight);
                    change = true;
                }

                if(change){
                    this.updateLightFrom(dirX, dirY);
                }
            }
        }
    }

    public void calcInitialSkylight(int x1, int y1, int x2, int y2){
        for(int x = x2; x >= x1; x--){
            for(int y = y2; y >= y1; y--){
                byte light = this.calcLight(x, y, true);
                this.setSkyLight(x, y, light);
            }
        }

        for(int x = x1; x <= x2; x++){
            for(int y = y1; y <= y2; y++){
                byte light = this.calcLight(x, y, true);
                this.setSkyLight(x, y, light);
            }
        }
    }

    protected byte calcLight(int x, int y, boolean isSky){
        byte maxLight = 0;

        for(Direction direction : Direction.REAL_DIRECTIONS){
            int dirX = x+direction.x;
            int dirY = y+direction.y;

            if(this.isPosLoaded(dirX, dirY)){
                byte light = isSky ? this.getSkyLight(dirX, dirY) : this.getArtificialLight(dirX, dirY);
                if(light > maxLight){
                    maxLight = light;
                }
            }
        }

        maxLight *= this.getTileModifier(x, y, isSky);

        byte emitted = this.getTileLight(x, y, isSky);
        if(emitted > maxLight){
            maxLight = emitted;
        }

        return (byte)Math.min(Constants.MAX_LIGHT, maxLight);
    }

    protected byte getTileLight(int x, int y, boolean isSky){
        Tile foreground = this.getTile(x, y);
        Tile background = this.getTile(TileLayer.BACKGROUND, x, y);

        if(foreground.isAir() && background.isAir()){
            if(isSky){
                return Constants.MAX_LIGHT;
            }
        }
        else{
            if(!isSky){
                byte foregroundLight = foreground.getLight(this, x, y, TileLayer.MAIN);
                byte backgroundLight = background.getLight(this, x, y, TileLayer.BACKGROUND);
                return (byte)Math.max(foregroundLight, backgroundLight);
            }
        }
        return 0;
    }

    protected float getTileModifier(int x, int y, boolean isSky){
        Tile foreground = this.getTile(x, y);

        if(!foreground.isAir()){
            return foreground.getTranslucentModifier(this, x, y, TileLayer.MAIN);
        }
        else{
            Tile background = this.getTile(TileLayer.BACKGROUND, x, y);
            if(!background.isAir()){
                return background.getTranslucentModifier(this, x, y, TileLayer.BACKGROUND);
            }
            else{
                return isSky ? 1.0F : 0.8F;
            }
        }
    }

    public float getSkylightModifier(){
        int noon = Constants.TIME_PER_DAY/2;
        if(this.info.currentWorldTime <= noon){
            return (float)this.info.currentWorldTime/(float)noon;
        }
        else{
            return 1F-(float)(this.info.currentWorldTime-noon)/(float)noon;
        }
    }

    public boolean isClient(){
        return false;
    }

    public static class WorldInfo{

        private final File dataFile;

        public long seed;
        public int totalTimeInWorld;
        public int currentWorldTime;

        public WorldInfo(File worldDirectory){
            this.dataFile = new File(worldDirectory, "world_info.dat");
        }

        public void load(){
            DataSet dataSet = new DataSet();
            dataSet.read(this.dataFile);

            this.seed = dataSet.getLong("seed");
            this.totalTimeInWorld = dataSet.getInt("total_time");
            this.currentWorldTime = dataSet.getInt("curr_time");
        }

        public void save(){
            DataSet dataSet = new DataSet();
            dataSet.addLong("seed", this.seed);
            dataSet.addInt("total_time", this.totalTimeInWorld);
            dataSet.addInt("curr_time", this.currentWorldTime);
            dataSet.write(this.dataFile);
        }

        public void toBuffer(ByteBuf buf){
            buf.writeLong(this.seed);
            buf.writeInt(this.totalTimeInWorld);
            buf.writeInt(this.currentWorldTime);
        }

        public void fromBuffer(ByteBuf buf){
            this.seed = buf.readLong();
            this.totalTimeInWorld = buf.readInt();
            this.currentWorldTime = buf.readInt();
        }
    }
}
