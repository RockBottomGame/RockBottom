package de.ellpeck.game.world;

import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.world.Chunk.TileLayer;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.tile.Tile;
import de.ellpeck.game.world.tile.entity.TileEntity;

import java.util.List;

public interface IWorld{

    Tile getTile(int x, int y);

    Tile getTile(TileLayer layer, int x, int y);

    byte getMeta(int x, int y);

    void setTile(int x, int y, Tile tile);

    void setTile(TileLayer layer, int x, int y, Tile tile);

    void setMeta(int x, int y, int meta);

    void setMeta(int x, int y, byte meta);

    void addEntity(Entity entity);

    void addTileEntity(TileEntity tile);

    void removeEntity(Entity entity);

    void removeTileEntity(int x, int y);

    TileEntity getTileEntity(int x, int y);

    List<Entity> getAllEntities();

    List<TileEntity> getAllTileEntities();

    List<Entity> getEntities(BoundBox area);

    List<BoundBox> getCollisions(BoundBox area);
}
