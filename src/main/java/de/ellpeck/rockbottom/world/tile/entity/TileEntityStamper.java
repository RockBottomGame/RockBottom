package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.inventory.TileInventory;

public class TileEntityStamper extends TileEntity{

    public final TileInventory inventory = new TileInventory(this, 1);

    public TileEntityStamper(IWorld world, int x, int y){
        super(world, x, y);
    }

    @Override
    public void save(DataSet set, boolean forSync){
        this.inventory.save(set);
    }

    @Override
    public void load(DataSet set, boolean forSync){
        this.inventory.load(set);
    }
}
