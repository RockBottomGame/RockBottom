package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;

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
