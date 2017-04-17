package de.ellpeck.game.inventory;

import de.ellpeck.game.item.ItemInstance;

public interface IInvChangeCallback{

    void onChange(IInventory inv, int slot, ItemInstance newInstance);

}
