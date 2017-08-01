package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.entity.IInventoryHolder;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.inventory.TileInventory;

import java.util.ArrayList;
import java.util.List;

public class TileEntityChest extends TileEntity implements IInventoryHolder{

    public final TileInventory inventory = new TileInventory(this, 20);
    public int openCount;

    public TileEntityChest(IWorld world, int x, int y){
        super(world, x, y);
    }

    @Override
    public void save(DataSet set, boolean forSync){
        if(!forSync){
            this.inventory.save(set);
        }
    }

    @Override
    public void load(DataSet set, boolean forSync){
        if(!forSync){
            this.inventory.load(set);
        }
    }

    @Override
    public IInventory getInventory(){
        return this.inventory;
    }

    @Override
    public List<Integer> getInputSlots(ItemInstance instance, Direction dir){
        return this.getOutputSlots(dir);
    }

    @Override
    public List<Integer> getOutputSlots(Direction dir){
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < this.inventory.getSlotAmount(); i++){
            list.add(i);
        }
        return list;
    }
}
