package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;

public class TileLadder extends TileBasic{

    public TileLadder(){
        super(AbstractGame.internalRes("ladder"));
    }

    @Override
    public boolean canClimb(IWorld world, int x, int y, TileLayer layer){
        return world.getTile(layer, x, y+1) instanceof TileLadder;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}
