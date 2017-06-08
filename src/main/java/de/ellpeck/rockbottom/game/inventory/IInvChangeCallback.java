package de.ellpeck.rockbottom.game.inventory;

import de.ellpeck.rockbottom.api.item.ItemInstance;

public interface IInvChangeCallback{

    void onChange(IInventory inv, int slot, ItemInstance newInstance);

}
