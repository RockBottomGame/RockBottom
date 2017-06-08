package de.ellpeck.rockbottom.game.inventory;

import de.ellpeck.rockbottom.api.item.ItemInstance;

public interface IInventory{

    void set(int id, ItemInstance instance);

    ItemInstance add(int id, int amount);

    ItemInstance remove(int id, int amount);

    ItemInstance get(int id);

    int getSlotAmount();

    void notifyChange(int slot);

    void addChangeCallback(IInvChangeCallback callback);

    void removeChangeCallback(IInvChangeCallback callback);

    default boolean containsItem(ItemInstance inst){
        return this.getItemIndex(inst) >= 0;
    }

    default int getItemIndex(ItemInstance inst){
        for(int i = 0; i < this.getSlotAmount(); i++){
            ItemInstance instance = this.get(i);
            if(instance != null && instance.isItemEqual(inst) && instance.getAmount() >= inst.getAmount()){
                return i;
            }
        }
        return -1;
    }
}
