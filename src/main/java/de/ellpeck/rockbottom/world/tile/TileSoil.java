package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileSoil extends TileBasic{

    public TileSoil(){
        super(ResourceName.intern("soil"));
    }

    @Override
    public boolean canGrassSpreadTo(IWorld world, int x, int y, TileLayer layer){
        return Util.RANDOM.nextInt(30) <= 0 && !world.getState(layer, x, y+1).getTile().isFullTile();
    }

    @Override
    public boolean canKeepPlants(IWorld world, int x, int y, TileLayer layer){
        return true;
    }
}
