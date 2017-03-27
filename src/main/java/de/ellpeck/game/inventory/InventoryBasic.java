package de.ellpeck.game.inventory;

import de.ellpeck.game.data.set.DataSet;
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
        return this.add(instance, simulate, false);
    }

    public boolean add(ItemInstance instance, boolean simulate, boolean fromBack){
        ItemInstance copy = instance.copy();

        for(int c = 0; c < this.slots.length; c++){
            int i = fromBack ? this.slots.length-1-c : c;
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
                        copy.remove(space);
                    }
                }
            }
        }
        return false;
    }

    public void save(DataSet set){
        for(int i = 0; i < this.slots.length; i++){
            ItemInstance slot = this.slots[i];

            if(slot != null){
                DataSet subset = new DataSet();
                slot.save(subset);
                set.addDataSet("item_"+i, subset);
            }
        }
    }

    public void load(DataSet set){
        for(int i = 0; i < this.slots.length; i++){
            DataSet subset = set.getDataSet("item_"+i);
            if(!subset.isEmpty()){
                this.slots[i] = ItemInstance.load(subset);
            }
            else{
                this.slots[i] = null;
            }
        }
    }
}
