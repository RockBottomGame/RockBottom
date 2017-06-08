package de.ellpeck.rockbottom.api.entity.player;

import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.TileLayer;

public interface IInteractionManager{

    boolean isToolEffective(AbstractEntityPlayer player, ItemInstance instance, Tile tile, TileLayer layer, int x, int y);

    TileLayer getBreakingLayer();

    int getBreakTileX();

    int getBreakTileY();

    float getBreakProgress();

    int getPlaceCooldown();

    int getMousedTileX();

    int getMousedTileY();
}
