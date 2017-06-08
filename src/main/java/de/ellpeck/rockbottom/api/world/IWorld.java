package de.ellpeck.rockbottom.api.world;

import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.game.world.entity.Entity;

import java.util.List;

public interface IWorld extends IChunkOrWorld{

    IChunk getChunkFromGridCoords(int gridX, int gridY);

    IChunk getChunk(double x, double y);

    boolean isPosLoaded(int x, int y);

    boolean isChunkLoaded(int x, int y);

    List<BoundBox> getCollisions(BoundBox area);

    int getIdForTile(Tile tile);

    Tile getTileForId(int id);

    WorldInfo getWorldInfo();

    void destroyTile(int x, int y, TileLayer layer, Entity destroyer, boolean shouldDrop);

    int getSpawnX();

    void causeLightUpdate(int x, int y);
}
