package de.ellpeck.rockbottom.inventory;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class InventoryPlayer extends Inventory{

    public int selectedSlot;

    public InventoryPlayer(EntityPlayer player){
        super(32);
        this.addChangeCallback((inv, slot) -> {
            int fullness = 0;
            for(int i = 0; i < inv.getSlotAmount(); i++){
                if(inv.get(i) != null){
                    fullness++;

                    if(fullness >= inv.getSlotAmount()/2){
                        player.getKnowledge().teachRecipe(ConstructionRegistry.chest, true);
                    }
                }
            }
        });
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
