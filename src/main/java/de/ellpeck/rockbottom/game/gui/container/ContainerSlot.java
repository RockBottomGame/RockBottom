package de.ellpeck.rockbottom.game.gui.container;

import de.ellpeck.rockbottom.game.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;

public class ContainerSlot{

    public final IInventory inventory;
    public final int slot;

    public final int x;
    public final int y;

    public ContainerSlot(IInventory inventory, int slot, int x, int y){
        this.inventory = inventory;
        this.slot = slot;
        this.x = x;
        this.y = y;
    }

    public boolean canPlace(ItemInstance instance){
        return true;
    }

    public boolean canRemove(){
        return true;
    }

    public void set(ItemInstance instance){
        this.inventory.set(this.slot, instance);
    }

    public ItemInstance get(){
        return this.inventory.get(this.slot);
    }
}
