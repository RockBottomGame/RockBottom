package de.ellpeck.game.world.gen.ore;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.world.tile.Tile;

public class WorldGenCoal extends WorldGenOre{

    @Override
    public int getHighestGridPos(){
        return -2;
    }

    @Override
    public int getMaxAmount(){
        return 3;
    }

    @Override
    public int getClusterRadiusX(){
        return 6;
    }

    @Override
    public int getClusterRadiusY(){
        return 4;
    }

    @Override
    public Tile getOreTile(){
        return ContentRegistry.TILE_COAL_ORE;
    }

    @Override
    public int getOreMeta(){
        return 0;
    }

    @Override
    public int getPriority(){
        return 200;
    }
}
