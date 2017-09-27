package de.ellpeck.rockbottom.world.entity.player;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.IKnowledgeManager;
import de.ellpeck.rockbottom.api.item.ItemInstance;

import java.util.*;

public class KnowledgeManager implements IKnowledgeManager{

    private final Map<IRecipe, RecipeKnowledge> recipeKnowledge = new HashMap<>();
    private final EntityPlayer player;

    public KnowledgeManager(EntityPlayer player){
        this.player = player;
    }

    public void save(DataSet set){
        int counter = 0;
        for(RecipeKnowledge knowledge : this.recipeKnowledge.values()){
            DataSet sub = new DataSet();

            sub.addString("name", knowledge.recipe.getName().toString());

            int inputCounter = 0;
            for(IUseInfo info : knowledge.knownInputs){
                sub.addInt("in_"+inputCounter, knowledge.recipe.getInputs().indexOf(info));
                inputCounter++;
            }
            sub.addInt("in_amount", inputCounter);

            int outputCounter = 0;
            for(ItemInstance instance : knowledge.knownOutputs){
                sub.addInt("out_"+outputCounter, knowledge.recipe.getOutputs().indexOf(instance));
                outputCounter++;
            }
            sub.addInt("out_amount", outputCounter);

            set.addDataSet("recipe_"+counter, sub);
            counter++;
        }
        set.addInt("recipe_amount", counter);
    }

    public void load(DataSet set){
        this.recipeKnowledge.clear();

        int amount = set.getInt("recipe_amount");
        for(int i = 0; i < amount; i++){
            DataSet sub = set.getDataSet("recipe_"+i);

            IRecipe recipe = RockBottomAPI.ALL_CONSTRUCTION_RECIPES.get(RockBottomAPI.createRes(sub.getString("name")));
            if(recipe != null){
                RecipeKnowledge knowledge = new RecipeKnowledge(recipe);

                int inputAmount = sub.getInt("in_amount");
                for(int j = 0; j < inputAmount; j++){
                    List<IUseInfo> inputs = knowledge.recipe.getInputs();
                    int input = sub.getInt("in_"+j);

                    if(input >= 0 && input < inputs.size()){
                        IUseInfo info = inputs.get(input);
                        if(info != null){
                            knowledge.knownInputs.add(info);
                        }
                    }
                }

                int outputAmount = sub.getInt("out_amount");
                for(int j = 0; j < outputAmount; j++){
                    List<ItemInstance> outputs = knowledge.recipe.getOutputs();
                    int output = sub.getInt("out_"+j);

                    if(output >= 0 && output < outputs.size()){
                        ItemInstance instance = outputs.get(output);
                        if(instance != null){
                            knowledge.knownOutputs.add(instance);
                        }
                    }
                }

                this.recipeKnowledge.put(recipe, knowledge);
            }
        }
    }

    @Override
    public boolean knowsRecipe(IRecipe recipe){
        return this.recipeKnowledge.containsKey(recipe);
    }

    @Override
    public boolean knowsIngredient(IRecipe recipe, IUseInfo info){
        RecipeKnowledge knowledge = this.recipeKnowledge.get(recipe);
        return knowledge != null && knowledge.knownInputs.contains(info);
    }

    @Override
    public boolean knowsOutput(IRecipe recipe, ItemInstance instance){
        RecipeKnowledge knowledge = this.recipeKnowledge.get(recipe);
        return knowledge != null && knowledge.knownOutputs.contains(instance);
    }

    @Override
    public void teachRecipe(IRecipe recipe, boolean teachAllParts){
        RecipeKnowledge knowledge = this.recipeKnowledge.computeIfAbsent(recipe, RecipeKnowledge::new);

        if(teachAllParts){
            knowledge.knownInputs.addAll(recipe.getInputs());
            knowledge.knownOutputs.addAll(recipe.getOutputs());
        }

        RockBottomAPI.logger().config("Taught player "+this.player.getName()+" with unique id "+this.player.getUniqueId()+" the entire recipe "+recipe.getName());
    }

    @Override
    public void teachIngredient(IRecipe recipe, IUseInfo info){
        if(recipe.getInputs().contains(info)){
            RecipeKnowledge knowledge = this.recipeKnowledge.computeIfAbsent(recipe, RecipeKnowledge::new);
            knowledge.knownInputs.add(info);

            RockBottomAPI.logger().config("Taught player "+this.player.getName()+" with unique id "+this.player.getUniqueId()+" ingredient "+info+" for recipe "+recipe.getName());
        }
        else{
            throw new IllegalArgumentException("Recipe "+recipe.getName()+" does not contain ingredient "+info);
        }
    }

    @Override
    public void teachOutput(IRecipe recipe, ItemInstance instance){
        if(recipe.getOutputs().contains(instance)){
            RecipeKnowledge knowledge = this.recipeKnowledge.computeIfAbsent(recipe, RecipeKnowledge::new);
            knowledge.knownOutputs.add(instance);

            RockBottomAPI.logger().config("Taught player "+this.player.getName()+" with unique id "+this.player.getUniqueId()+" output "+instance+" for recipe "+recipe.getName());
        }
        else{
            throw new IllegalArgumentException("Recipe "+recipe.getName()+" does not contain output "+instance);
        }
    }

    private static class RecipeKnowledge{

        private final Set<IUseInfo> knownInputs = new HashSet<>();
        private final Set<ItemInstance> knownOutputs = new HashSet<>();

        private final IRecipe recipe;

        public RecipeKnowledge(IRecipe recipe){
            this.recipe = recipe;
        }

        @Override
        public String toString(){
            return "{"+this.recipe.getName()+", in="+this.knownInputs+", out="+this.knownOutputs+"}";
        }

        @Override
        public boolean equals(Object o){
            if(this == o){
                return true;
            }
            else if(o instanceof RecipeKnowledge){
                RecipeKnowledge that = (RecipeKnowledge)o;
                return this.knownInputs.equals(that.knownInputs) && this.knownOutputs.equals(that.knownOutputs) && this.recipe.equals(that.recipe);
            }
            else{
                return false;
            }
        }

        @Override
        public int hashCode(){
            int result = this.knownInputs.hashCode();
            result = 31*result+this.knownOutputs.hashCode();
            result = 31*result+this.recipe.hashCode();
            return result;
        }
    }
}
