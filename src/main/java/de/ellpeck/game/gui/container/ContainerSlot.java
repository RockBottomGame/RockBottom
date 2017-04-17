package de.ellpeck.game.gui.container;

import de.ellpeck.game.inventory.IInventory;
import de.ellpeck.game.item.ItemInstance;

public class ContainerSlot{

    private final ItemContainer container;

    private final IInventory inventory;
    private final int slot;

    public final int x;
    public final int y;

    public ContainerSlot(ItemContainer container, IInventory inventory, int slot, int x, int y){
        this.container = container;
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
