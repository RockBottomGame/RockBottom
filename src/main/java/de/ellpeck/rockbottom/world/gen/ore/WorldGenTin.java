package de.ellpeck.rockbottom.world.gen.ore;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.world.gen.WorldGenOre;

public class WorldGenTin extends WorldGenOre{

    @Override
    public int getHighestGridPos(){
        return -4;
    }

    @Override
    public int getLowestGridPos(){
        return -10;
    }

    @Override
    public int getMaxAmount(){
        return 2;
    }

    @Override
    public int getClusterRadiusX(){
        return 15;
    }

    @Override
    public int getClusterRadiusY(){
        return 4;
    }

    @Override
    public TileState getOreState(){
        return GameContent.TILE_TIN_ORE.getDefState();
    }

    @Override
    public int getPriority(){
        return 220;
    }
}
