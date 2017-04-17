package de.ellpeck.game.gui.container;

import de.ellpeck.game.inventory.IInventory;
import de.ellpeck.game.item.ItemInstance;

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

    public void set(ItemInstance instance){
        this.inventory.set(this.slot, instance);
    }

    public ItemInstance get(){
        return this.inventory.get(this.slot);
    }
}
