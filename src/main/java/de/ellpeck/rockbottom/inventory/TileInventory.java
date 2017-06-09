package de.ellpeck.rockbottom.inventory;

import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;

public class TileInventory extends Inventory{

    public TileInventory(TileEntity tile, int slotAmount){
        super(slotAmount);
        this.addChangeCallback((inv, slot, newInstance) -> tile.world.setDirty(tile.x, tile.y));
    }
}
