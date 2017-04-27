package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.construction.ConstructionList;
import de.ellpeck.rockbottom.construction.IRecipe;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.world.entity.EntityItem;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class ContainerInventory extends ItemContainer{

    public ContainerInventory(EntityPlayer player){
        super(player, player.inv);
        this.addPlayerInventory(player, 0, 0);
    }

    @Override
    public int getUnboundId(){
        return 0;
    }

    public static void doManualCraft(EntityPlayer player, IRecipe recipe){
        if(ConstructionList.matchesInputs(recipe, player.inv.getItems())){
            for(ItemInstance input : recipe.getInputs()){
                for(int i = 0; i < player.inv.getSlotAmount(); i++){
                    ItemInstance inv = player.inv.get(i);

                    if(inv != null && inv.isItemEqual(input) && inv.getAmount() >= input.getAmount()){
                        inv.remove(input.getAmount());

                        if(inv.getAmount() <= 0){
                            player.inv.set(i, null);
                        }
                        else{
                            player.inv.notifyChange(i);
                        }

                        break;
                    }
                }
            }

            for(ItemInstance output : recipe.getOutputs()){
                ItemInstance left = player.inv.addExistingFirst(output, false);
                if(left != null){
                    EntityItem.spawn(player.world, left, player.x, player.y, 0F, 0F);
                }
            }
        }
    }
}
