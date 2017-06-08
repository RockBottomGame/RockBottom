package de.ellpeck.rockbottom.game.inventory;

import de.ellpeck.rockbottom.game.item.ItemInstance;

public interface IInvChangeCallback{

    void onChange(IInventory inv, int slot, ItemInstance newInstance);

}
