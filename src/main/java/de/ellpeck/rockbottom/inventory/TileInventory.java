package de.ellpeck.rockbottom.inventory;

import de.ellpeck.rockbottom.world.tile.entity.TileEntity;

public class TileInventory extends Inventory{

    public TileInventory(TileEntity tile, int slotAmount){
        super(slotAmount);
        this.addChangeCallback((inv, slot, newInstance) -> tile.world.setDirty(tile.x, tile.y));
    }
}
