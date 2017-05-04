package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.inventory.IInventory;
import de.ellpeck.rockbottom.item.ItemInstance;

import java.util.List;

public interface IRecipe{

    List<ItemInstance> getInputs();

    List<ItemInstance> getOutputs();

    static boolean matchesInv(IRecipe recipe, IInventory inventory){
        for(ItemInstance inst : recipe.getInputs()){
            if(!inventory.containsItem(inst)){
                return false;
            }
        }
        return true;
    }
}
