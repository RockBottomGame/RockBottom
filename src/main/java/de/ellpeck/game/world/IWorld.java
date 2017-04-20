package de.ellpeck.game.world;

import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.tile.Tile;
import de.ellpeck.game.world.tile.entity.TileEntity;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public interface IWorld{

    Tile getTile(int x, int y);

    Tile getTile(TileLayer layer, int x, int y);

    int getMeta(int x, int y);

    int getMeta(TileLayer layer, int x, int y);

    void setTile(int x, int y, Tile tile);

    void setTile(TileLayer layer, int x, int y, Tile tile);

    void setMeta(int x, int y, int meta);

    void setMeta(TileLayer layer, int x, int y, int meta);

    void addEntity(Entity entity);

    void addTileEntity(TileEntity tile);

    void removeEntity(Entity entity);

    void removeTileEntity(int x, int y);

    TileEntity getTileEntity(int x, int y);

    <T extends TileEntity> T getTileEntity(int x, int y, Class<T> tileClass);

    List<Entity> getAllEntities();

    List<TileEntity> getAllTileEntities();

    Entity getEntity(UUID id);

    List<Entity> getEntities(BoundBox area);

    List<Entity> getEntities(BoundBox area, Predicate<Entity> test);

    <T extends Entity> List<T> getEntities(BoundBox area, Class<T> type);

    <T extends Entity> List<T> getEntities(BoundBox area, Class<T> type, Predicate<T> test);

    List<BoundBox> getCollisions(BoundBox area);

    byte getCombinedLight(int x, int y);

    byte getSkyLight(int x, int y);

    byte getArtificialLight(int x, int y);

    void setSkyLight(int x, int y, byte light);

    void setArtificialLight(int x, int y, byte light);

    boolean isPosLoaded(int x, int y);

    boolean isChunkLoaded(int x, int y);

    void scheduleUpdate(int x, int y, TileLayer layer, int time);

    void setDirty(int x, int y);

    int getLowestAirUpwards(TileLayer layer, int x, int y);
}
