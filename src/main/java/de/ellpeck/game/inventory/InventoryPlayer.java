package de.ellpeck.game.inventory;

import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.world.entity.player.EntityPlayer;

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
