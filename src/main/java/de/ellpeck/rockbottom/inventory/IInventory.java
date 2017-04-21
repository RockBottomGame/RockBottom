package de.ellpeck.rockbottom.inventory;

import de.ellpeck.rockbottom.item.ItemInstance;

public interface IInventory{

    void set(int id, ItemInstance instance);

    ItemInstance get(int id);

    int getSlotAmount();

    void notifyChange(int slot);

    void addChangeCallback(IInvChangeCallback callback);

    void removeChangeCallback(IInvChangeCallback callback);
}
