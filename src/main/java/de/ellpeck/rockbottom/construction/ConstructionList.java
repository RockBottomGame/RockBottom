package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.item.ItemInstance;

import java.util.ArrayList;
import java.util.List;

public class ConstructionList<T extends IRecipe>{

    private final List<T> list = new ArrayList<>();

    public void add(T recipe){
        this.list.add(recipe);
    }

    public List<T> fromInputs(List<ItemInstance> inputs){
        List<T> possibleRecipes = new ArrayList<>();
        for(T recipe : this.list){
            if(this.matchesInputs(recipe, inputs)){
                possibleRecipes.add(recipe);
            }
        }
        return possibleRecipes;
    }

    public T firstFromInputs(List<ItemInstance> inputs){
        for(T recipe : this.list){
            if(this.matchesInputs(recipe, inputs)){
                return recipe;
            }
        }
        return null;
    }

    public boolean matchesInputs(IRecipe recipe, List<ItemInstance> inputs){
        List<ItemInstance> recipeInputs = new ArrayList<>(recipe.getInputs());

        for(ItemInstance testInput : inputs){
            if(testInput != null){
                for(ItemInstance recipeInput : recipeInputs){
                    if(testInput.isItemEqual(recipeInput)){
                        recipeInputs.remove(recipeInput);
                        break;
                    }
                }
            }
        }

        return recipeInputs.isEmpty();
    }
}
