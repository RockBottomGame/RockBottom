package de.ellpeck.rockbottom.api.world;

import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.game.world.entity.Entity;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;
import io.netty.channel.Channel;

import java.util.List;
import java.util.UUID;

public interface IWorld extends IChunkOrWorld{

    IChunk getChunkFromGridCoords(int gridX, int gridY);

    IChunk getChunk(double x, double y);

    boolean isPosLoaded(int x, int y);

    boolean isChunkLoaded(int x, int y);

    List<BoundBox> getCollisions(BoundBox area);

    int getIdForTile(Tile tile);

    Tile getTileForId(int id);

    WorldInfo getWorldInfo();

    NameToIndexInfo getTileRegInfo();

    void notifyNeighborsOfChange(int x, int y, TileLayer layer);

    EntityPlayer createPlayer(UUID id, Channel channel);

    EntityPlayer getPlayer(UUID id);

    void destroyTile(int x, int y, TileLayer layer, Entity destroyer, boolean shouldDrop);

    int getSpawnX();

    void causeLightUpdate(int x, int y);

    void unloadChunk(IChunk chunk);

    boolean isClient();

    void savePlayer(EntityPlayer player);
}
