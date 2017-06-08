package de.ellpeck.rockbottom.game.gui.container;

import de.ellpeck.rockbottom.game.inventory.IInventory;
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
