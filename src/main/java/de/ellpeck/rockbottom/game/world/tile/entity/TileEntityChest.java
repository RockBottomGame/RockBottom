package de.ellpeck.rockbottom.game.world.tile.entity;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.inventory.TileInventory;

public class TileEntityChest extends TileEntity{

    public final TileInventory inventory = new TileInventory(this, 20);
    public int openCount;

    public TileEntityChest(IWorld world, int x, int y){
        super(world, x, y);
    }

    @Override
    public void save(DataSet set){
        this.inventory.save(set);
    }

    @Override
    public void load(DataSet set){
        this.inventory.load(set);
    }
}
