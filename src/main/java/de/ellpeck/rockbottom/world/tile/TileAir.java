package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;

public class TileAir extends Tile{

    public TileAir(int id){
        super(id, "air");
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean canBreak(World world, int x, int y, TileLayer layer){
        return false;
    }

    @Override
    protected boolean hasItem(){
        return false;
    }

    @Override
    public boolean canReplace(World world, int x, int y, TileLayer layer, Tile replacementTile){
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
