package de.ellpeck.rockbottom.inventory;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class InventoryPlayer extends Inventory{

    public int selectedSlot;

    public InventoryPlayer(EntityPlayer player){
        super(32);
        this.addChangeCallback(player);
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
