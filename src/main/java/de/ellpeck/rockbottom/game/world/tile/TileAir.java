package de.ellpeck.rockbottom.game.world.tile;

import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.game.world.World;

public class TileAir extends Tile{

    public TileAir(){
        super("air");
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean canBreak(IWorld world, int x, int y, TileLayer layer){
        return false;
    }

    @Override
    protected boolean hasItem(){
        return false;
    }

    @Override
    public boolean canReplace(IWorld world, int x, int y, TileLayer layer, Tile replacementTile){
        return true;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public boolean isAir(){
        return true;
    }
}
