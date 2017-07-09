package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;

public class TileLadder extends TileBasic{

    public TileLadder(){
        super(AbstractGame.internalRes("ladder"));
    }

    @Override
    public boolean canClimb(IWorld world, int x, int y, TileLayer layer, Entity entity){
        return Util.floor(entity.y) == y;
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
