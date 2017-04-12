package de.ellpeck.game.inventory;

import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.item.ItemInstance;

public class Inventory implements IInventory{

    private final Runnable changeCallback;
    private final ItemInstance[] slots;

    public Inventory(int slotAmount){
        this(slotAmount, null);
    }

    public Inventory(int slotAmount, Runnable changeCallback){
        this.slots = new ItemInstance[slotAmount];
        this.changeCallback = changeCallback;
    }

    @Override
    public void set(int id, ItemInstance instance){
        this.slots[id] = instance;
        this.notifyChange();
    }

    @Override
    public ItemInstance get(int id){
        return this.slots[id];
    }

    @Override
    public int getSlotAmount(){
        return this.slots.length;
    }

    @Override
    public void notifyChange(){
        if(this.changeCallback != null){
            this.changeCallback.run();
        }
    }

    public ItemInstance add(ItemInstance instance, boolean simulate){
        ItemInstance copy = instance.copy();

        for(int i = 0; i < this.slots.length; i++){
            copy = this.addToSlot(i, copy, simulate);

            if(copy == null){
                return null;
            }
        }

        return copy;
    }

    public ItemInstance addExistingFirst(ItemInstance instance, boolean simulate){
        ItemInstance copy = instance.copy();

        for(int i = 0; i < 2; i++){
            for(int j = 0; j < this.slots.length; j++){
                if(i == 1 || (this.slots[j] != null && this.slots[j].isItemEqual(instance))){
                    copy = this.addToSlot(j, copy, simulate);

                    if(copy == null){
                        return null;
                    }
                }
            }
        }

        return copy;
    }

    public ItemInstance addToSlot(int slot, ItemInstance instance, boolean simulate){
        ItemInstance slotInst = this.slots[slot];

        if(slotInst == null){
            if(!simulate){
                this.set(slot, instance);
            }
            return null;
        }
        else if(slotInst.isItemEqual(instance)){
            int space = slotInst.getItem().getMaxAmount()-slotInst.getAmount();

            if(space >= instance.getAmount()){
                if(!simulate){
                    slotInst.add(instance.getAmount());
                    this.notifyChange();
                }
                return null;
            }
            else{
                if(!simulate){
                    slotInst.add(space);
                    instance.remove(space);
                    this.notifyChange();
                }
            }
        }
        return instance;
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
