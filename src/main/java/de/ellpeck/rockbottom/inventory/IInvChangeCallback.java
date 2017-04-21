package de.ellpeck.rockbottom.inventory;

import de.ellpeck.rockbottom.item.ItemInstance;

public interface IInvChangeCallback{

    void onChange(IInventory inv, int slot, ItemInstance newInstance);

}
