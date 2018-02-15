package de.ellpeck.rockbottom.inventory;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class InventoryPlayer extends Inventory{

    public int selectedSlot;

    public InventoryPlayer(EntityPlayer player){
        super(32);

        if(!player.world.isClient()){
            this.addChangeCallback((inv, slot) -> {
                if(!ConstructionRegistry.chest.isKnown(player)){
                    int fullness = 0;
                    for(int i = 0; i < inv.getSlotAmount(); i++){
                        if(inv.get(i) != null){
                            fullness++;

                            if(fullness >= inv.getSlotAmount()/2){
                                player.getKnowledge().teachRecipe(ConstructionRegistry.chest, true);
                            }
                        }
                    }
                }
            });

            this.addChangeCallback((inv, slot) -> {
                if(!ConstructionRegistry.grassTorch.isKnown(player)){
                    ItemInstance instance = inv.get(slot);
                    if(instance != null && instance.getItem() == GameContent.TILE_GRASS_TORCH.getItem()){
                        player.getKnowledge().teachRecipe(ConstructionRegistry.grassTorch, true);
                    }
                }
            });
        }
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
