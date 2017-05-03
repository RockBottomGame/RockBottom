package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.data.set.DataSet;
import de.ellpeck.rockbottom.inventory.TileInventory;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.world.World;

public class TileEntitySeparator extends TileEntity{

    private static final int INPUT = 0;
    private static final int OUTPUT = 1;
    private static final int SLAG = 2;
    private static final int COAL = 3;

    public final TileInventory inventory = new TileInventory(this, 4);

    public TileEntitySeparator(World world, int x, int y){
        super(world, x, y);
    }

    @Override
    public void update(RockBottom game){
        if(!NetHandler.isClient()){

        }
    }

    @Override
    public void save(DataSet set){
        super.save(set);
        this.inventory.save(set);
    }

    @Override
    public void load(DataSet set){
        super.load(set);
        this.inventory.load(set);
    }
}
