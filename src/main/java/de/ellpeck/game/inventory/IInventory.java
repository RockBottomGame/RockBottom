package de.ellpeck.game.inventory;

import de.ellpeck.game.item.ItemInstance;

public interface IInventory{

    void set(int id, ItemInstance instance);

    ItemInstance get(int id);

    int getSlotAmount();

    void notifyChange();
}
