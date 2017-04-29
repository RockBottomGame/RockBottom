package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.inventory.IInventory;
import de.ellpeck.rockbottom.item.ItemInstance;

import java.util.ArrayList;
import java.util.List;

public class ConstructionList<T extends IRecipe>{

    private final List<T> list = new ArrayList<>();

    public void add(T recipe){
        this.list.add(recipe);
    }

    public List<T> getUnmodifiable(){
        return new ArrayList<>(this.list);
    }

    public T get(int id){
        if(id >= 0 && this.list.size() > id){
            return this.list.get(id);
        }
        else{
            return null;
        }
    }

    public int getId(T recipe){
        return this.list.indexOf(recipe);
    }

    public static boolean matchesInv(IRecipe recipe, IInventory inventory){
        for(ItemInstance inst : recipe.getInputs()){
            if(!inventory.containsItem(inst)){
                return false;
            }
        }
        return true;
    }
}
