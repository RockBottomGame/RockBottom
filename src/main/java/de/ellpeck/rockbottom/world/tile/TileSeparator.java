package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.tile.entity.TileEntity;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySeparator;

public class TileSeparator extends MultiTile{

    public TileSeparator(int id){
        super(id, "separator");
    }

    @Override
    public boolean canProvideTileEntity(){
        return true;
    }

    @Override
    public TileEntity provideTileEntity(World world, int x, int y){
        return this.isMainPos(x, y, world.getMeta(x, y)) ? new TileEntitySeparator(world, x, y) : null;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    protected boolean[][] makeStructure(){
        return new boolean[][]{
                new boolean[]{false, true},
                new boolean[]{true, true},
                new boolean[]{true, true},
        };
    }

    @Override
    public int getWidth(){
        return 2;
    }

    @Override
    public int getHeight(){
        return 3;
    }

    @Override
    public int getMainX(){
        return 1;
    }

    @Override
    public int getMainY(){
        return 0;
    }
}
