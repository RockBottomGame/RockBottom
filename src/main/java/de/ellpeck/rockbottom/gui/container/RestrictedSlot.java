package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;

import java.util.function.Predicate;

public class RestrictedSlot extends ContainerSlot{

    private final Predicate<ItemInstance> test;

    public RestrictedSlot(IInventory inventory, int slot, int x, int y, Predicate<ItemInstance> test){
        super(inventory, slot, x, y);
        this.test = test;
    }

    @Override
    public boolean canPlace(ItemInstance instance){
        return this.test.test(instance);
    }
}
