package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.inventory.TileInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.entity.IInventoryHolder;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Collections;
import java.util.List;

public class TileEntityChest extends TileEntity implements IInventoryHolder{

    private final Inventory inventory = new TileInventory(this, 20);
    private final List<Integer> inputOutputSlots = Collections.unmodifiableList(Util.makeIntList(0, this.inventory.getSlotAmount()));

    public TileEntityChest(IWorld world, int x, int y, TileLayer layer){
        super(world, x, y, layer);
    }

    @Override
    public IInventory getInventory(){
        return this.inventory;
    }

    @Override
    public List<Integer> getInputSlots(ItemInstance instance, Direction dir){
        return this.inputOutputSlots;
    }

    @Override
    public List<Integer> getOutputSlots(Direction dir){
        return this.inputOutputSlots;
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
}
