package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TileRemainsGoo extends TileBasic{

    public TileRemainsGoo(){
        super(RockBottomAPI.createInternalRes("remains_goo"));
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.LIQUIDS;
    }

    @Override
    public boolean isLiquid(){
        return true;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public boolean canBreak(IWorld world, int x, int y, TileLayer layer){
        return false;
    }

    @Override
    protected boolean hasItem(){
        return false;
    }
}
