package de.ellpeck.rockbottom.game.construction;

import de.ellpeck.rockbottom.game.inventory.IInventory;
import de.ellpeck.rockbottom.game.item.ItemInstance;

import java.util.List;

public interface IRecipe{

    static boolean matchesInv(IRecipe recipe, IInventory inventory){
        for(ItemInstance inst : recipe.getInputs()){
            if(!inventory.containsItem(inst)){
                return false;
            }
        }
        return true;
    }

    List<ItemInstance> getInputs();

    List<ItemInstance> getOutputs();
}
