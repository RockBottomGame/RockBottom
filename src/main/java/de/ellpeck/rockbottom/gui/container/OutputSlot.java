package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;

public class OutputSlot extends ContainerSlot{

    public OutputSlot(IInventory inventory, int slot, int x, int y){
        super(inventory, slot, x, y);
    }

    @Override
    public boolean canPlace(ItemInstance instance){
        return false;
    }
}
