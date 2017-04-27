package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.item.ItemInstance;

import java.util.ArrayList;
import java.util.List;

public class ConstructionList<T extends IRecipe>{

    private final List<T> list = new ArrayList<>();

    public void add(T recipe){
        this.list.add(recipe);
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

    public List<T> fromInputs(List<ItemInstance> inputs){
        List<T> possibleRecipes = new ArrayList<>();
        for(T recipe : this.list){
            if(matchesInputs(recipe, inputs)){
                possibleRecipes.add(recipe);
            }
        }
        return possibleRecipes;
    }

    public T firstFromInputs(List<ItemInstance> inputs){
        for(T recipe : this.list){
            if(matchesInputs(recipe, inputs)){
                return recipe;
            }
        }
        return null;
    }

    public static boolean matchesInputs(IRecipe recipe, List<ItemInstance> inputs){
        List<ItemInstance> recipeInputs = new ArrayList<>(recipe.getInputs());

        for(ItemInstance testInput : inputs){
            if(testInput != null){
                for(ItemInstance recipeInput : recipeInputs){
                    if(testInput.isItemEqual(recipeInput) && testInput.getAmount() >= recipeInput.getAmount()){
                        recipeInputs.remove(recipeInput);
                        break;
                    }
                }
            }
        }

        return recipeInputs.isEmpty();
    }
}
