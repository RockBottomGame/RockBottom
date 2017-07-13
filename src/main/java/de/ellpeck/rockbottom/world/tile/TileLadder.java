package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.tile.Tile;
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
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
        return super.canPlace(world, x, y, layer) && this.isSuitableLadderPos(world, x, y);
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        return this.isSuitableLadderPos(world, x, y);
    }

    private boolean isSuitableLadderPos(IWorld world, int x, int y){
        Tile tileBelow = world.getTile(x, y-1);
        Tile tileBackground = world.getTile(TileLayer.BACKGROUND, x, y);
        return tileBelow.isFullTile() || tileBackground.isFullTile() || tileBelow instanceof TileLadder;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}
