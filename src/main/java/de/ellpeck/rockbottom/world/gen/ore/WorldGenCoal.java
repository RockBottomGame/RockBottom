package de.ellpeck.rockbottom.world.gen.ore;

import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.ContentRegistry;

public class WorldGenCoal extends WorldGenOre{

    @Override
    public int getHighestGridPos(){
        return -1;
    }

    @Override
    public int getMaxAmount(){
        return 6;
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
