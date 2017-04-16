package de.ellpeck.game.gui.container;

import de.ellpeck.game.inventory.IInventory;

public class ContainerSlot{

    public final int x;
    public final int y;

    public final IInventory inventory;
    public final int slot;

    public ContainerSlot(IInventory inventory, int slot, int x, int y){
        this.inventory = inventory;
        this.slot = slot;
        this.x = x;
        this.y = y;
    }

}
