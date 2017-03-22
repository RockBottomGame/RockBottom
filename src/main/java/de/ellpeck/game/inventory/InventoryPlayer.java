package de.ellpeck.game.inventory;

import de.ellpeck.game.data.set.DataSet;

public class InventoryPlayer extends InventoryBasic{

    public int selectedSlot;

    public InventoryPlayer(){
        super(32);
    }

    @Override
    public void save(DataSet set){
        super.save(set);

        set.addInt("selected_slot", this.selectedSlot);
    }

    @Override
    public void load(DataSet set){
        super.load(set);

        this.selectedSlot = set.getInt("selected_slot");
    }
}
