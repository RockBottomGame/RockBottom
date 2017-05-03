package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.tile.entity.TileEntity;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySeparator;

public class TileSeparator extends TileBasic{

    public TileSeparator(int id){
        super(id, "separator");
    }

    @Override
    public boolean providesTileEntity(){
        return true;
    }

    @Override
    public TileEntity provideTileEntity(World world, int x, int y){
        return new TileEntitySeparator(world, x, y);
    }
}
