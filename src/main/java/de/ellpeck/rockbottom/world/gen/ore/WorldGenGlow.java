package de.ellpeck.rockbottom.world.gen.ore;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.world.gen.WorldGenOre;

public class WorldGenGlow extends WorldGenOre{


    @Override
    public int getHighestGridPos(){
        return -2;
    }

    @Override
    public int getMaxAmount(){
        return 2;
    }

    @Override
    public int getClusterRadiusX(){
        return 3;
    }

    @Override
    public int getClusterRadiusY(){
        return 3;
    }

    @Override
    public TileState getOreState(){
        return GameContent.TILE_GLOW_ORE.getDefState();
    }

    @Override
    public int getPriority(){
        return 200;
    }
}
