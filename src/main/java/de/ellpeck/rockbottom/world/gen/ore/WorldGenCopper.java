package de.ellpeck.rockbottom.world.gen.ore;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.world.gen.WorldGenOre;

public class WorldGenCopper extends WorldGenOre{

    @Override
    public int getPriority(){
        return 210;
    }

    @Override
    public int getHighestGridPos(){
        return -3;
    }

    @Override
    public int getLowestGridPos(){
        return -6;
    }

    @Override
    public int getMaxAmount(){
        return 3;
    }

    @Override
    public int getClusterRadiusX(){
        return 12;
    }

    @Override
    public int getClusterRadiusY(){
        return 3;
    }

    @Override
    public TileState getOreState(){
        return GameContent.TILE_COPPER_ORE.getDefState();
    }
}
