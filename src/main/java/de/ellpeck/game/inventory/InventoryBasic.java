package de.ellpeck.game.inventory;

import de.ellpeck.game.item.ItemInstance;

public class InventoryBasic implements IInventory{

    private final ItemInstance[] slots;

    public InventoryBasic(int slotAmount){
        this.slots = new ItemInstance[slotAmount];
    }

    @Override
    public void set(int id, ItemInstance instance){
        this.slots[id] = instance;
    }

    @Override
    public ItemInstance get(int id){
        return this.slots[id];
    }

    @Override
    public int getSlotAmount(){
        return this.slots.length;
    }

    public boolean add(ItemInstance instance, boolean simulate){
        ItemInstance copy = instance.copy();

        for(int i = 0; i < this.slots.length; i++){
            ItemInstance slot = this.slots[i];

            if(slot == null){
                if(!simulate){
                    this.slots[i] = copy;
                }
                return true;
            }
            else if(slot.isItemEqual(copy)){
                int space = slot.getItem().getMaxAmount()-slot.getAmount();

                if(space >= copy.getAmount()){
                    if(!simulate){
                        slot.add(copy.getAmount());
                    }
                    return true;
                }
                else{
                    if(!simulate){
                        slot.add(space);
                        copy.add(-space);
                    }
                }
            }
        }
        return false;
    }
}
