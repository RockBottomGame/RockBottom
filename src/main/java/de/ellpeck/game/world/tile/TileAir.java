package de.ellpeck.game.world.tile;

import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.World;

public class TileAir extends Tile{

    public TileAir(int id){
        super(id);
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean canBreak(IWorld world, int x, int y, Chunk.TileLayer layer){
        return false;
    }

    @Override
    protected boolean hasItem(){
        return false;
    }

    @Override
    public boolean canReplace(World world, int x, int y, Chunk.TileLayer layer){
        return true;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}
