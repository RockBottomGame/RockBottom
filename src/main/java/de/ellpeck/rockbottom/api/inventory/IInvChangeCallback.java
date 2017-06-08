package de.ellpeck.rockbottom.api.inventory;

import de.ellpeck.rockbottom.api.item.ItemInstance;

public interface IInvChangeCallback{

    void onChange(IInventory inv, int slot, ItemInstance newInstance);

}
