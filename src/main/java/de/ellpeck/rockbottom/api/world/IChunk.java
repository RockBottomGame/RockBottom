package de.ellpeck.rockbottom.api.world;

import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.MutableInt;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;

import java.util.List;
import java.util.Map;

public interface IChunk extends IChunkOrWorld{

    List<EntityPlayer> getPlayersInRange();

    List<EntityPlayer> getPlayersLeftRange();

    Map<EntityPlayer, MutableInt> getLeftPlayerTimers();

    int getGridX();

    int getGridY();

    IWorld getWorld();

    int getX();

    int getY();

    Tile getTileInner(TileLayer layer, int x, int y);

    Tile getTileInner(int x, int y);

    byte getMetaInner(TileLayer layer, int x, int y);

    void setTileInner(int x, int y, Tile tile, int meta);

    void setTileInner(int x, int y, Tile tile);

    void setTileInner(TileLayer layer, int x, int y, Tile tile);

    void setTileInner(TileLayer layer, int x, int y, Tile tile, int meta);

    void setMetaInner(TileLayer layer, int x, int y, int meta);

    byte getSkylightInner(int x, int y);

    void setSkylightInner(int x, int y, byte light);

    byte getArtificialLightInner(int x, int y);

    void setArtificialLightInner(int x, int y, byte light);
}
