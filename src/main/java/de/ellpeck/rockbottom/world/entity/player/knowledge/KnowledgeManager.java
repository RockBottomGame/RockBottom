package de.ellpeck.rockbottom.world.entity.player.knowledge;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.entity.player.knowledge.Information;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class KnowledgeManager implements IKnowledgeManager{

    private final Map<IResourceName, Information> information = new HashMap<>();

    public void save(DataSet set){
        int counter = 0;
        for(Information information : this.information.values()){
            DataSet sub = new DataSet();

            sub.addString("name", information.getName().toString());
            sub.addString("reg_name", information.getRegistryName().toString());
            information.save(sub, this);

            set.addDataSet("info_"+counter, sub);
            System.out.println("Saved information "+information.getName());

            counter++;
        }
        set.addInt("info_amount", counter);
    }

    public void load(DataSet set){
        this.information.clear();

        int amount = set.getInt("info_amount");
        for(int i = 0; i < amount; i++){
            DataSet sub = set.getDataSet("info_"+i);

            IResourceName regName = RockBottomAPI.createRes(sub.getString("reg_name"));
            IResourceName name = RockBottomAPI.createRes(sub.getString("name"));

            Information information = loadInformation(regName, name);
            if(information != null){
                System.out.println("Loaded information "+information.getName());
                this.information.put(name, information);
                information.load(sub, this);
            }
            else{
                RockBottomAPI.logger().warning("Couldn't load information with registry name "+regName+" and name "+name);
            }
        }
    }

    private static Information loadInformation(IResourceName regName, IResourceName name){
        Class<? extends Information> infoClass = RockBottomAPI.INFORMATION_REGISTRY.get(regName);

        try{
            return infoClass.getConstructor(IResourceName.class).newInstance(name);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't initialize information with registry name "+regName+" and name "+name, e);
            return null;
        }
    }

    @Override
    public boolean knowsRecipe(IRecipe recipe){
        return this.knowsInformation(RecipeInformation.getInfoName(recipe));
    }

    @Override
    public boolean knowsIngredient(IRecipe recipe, IUseInfo info){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);
        return information != null && information.knownInputs.contains(info);
    }

    @Override
    public boolean knowsOutput(IRecipe recipe, ItemInstance instance){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);
        return information != null && information.knownOutputs.contains(instance);
    }

    @Override
    public boolean knowsInformation(IResourceName name){
        return this.information.containsKey(name);
    }

    @Override
    public Information getInformation(IResourceName name){
        return this.information.get(name);
    }

    @Override
    public <T extends Information> T getInformation(IResourceName name, Class<T> infoClass){
        Information info = this.getInformation(name);
        if(info != null && infoClass.isAssignableFrom(info.getClass())){
            return (T)info;
        }
        else{
            return null;
        }
    }

    @Override
    public void teachRecipe(IRecipe recipe, boolean teachAllParts){
        RecipeInformation information = new RecipeInformation(recipe);
        if(teachAllParts){
            information.knownInputs.addAll(recipe.getInputs());
            information.knownOutputs.addAll(recipe.getOutputs());
        }
        this.teachInformation(information);
    }

    @Override
    public void teachIngredient(IRecipe recipe, IUseInfo info){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);

        if(information == null){
            information = new RecipeInformation(recipe);
            this.teachInformation(information);
        }

        information.knownInputs.add(info);
    }

    @Override
    public void teachOutput(IRecipe recipe, ItemInstance instance){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);

        if(information == null){
            information = new RecipeInformation(recipe);
            this.teachInformation(information);
        }

        information.knownOutputs.add(instance);
    }

    @Override
    public void teachInformation(Information information){
        this.information.put(information.getName(), information);
    }

    @Override
    public void forgetRecipe(IRecipe recipe, boolean forgetAllParts){
        this.forgetInformation(RecipeInformation.getInfoName(recipe));
    }

    @Override
    public void forgetIngredient(IRecipe recipe, IUseInfo info){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);
        if(information != null){
            information.knownInputs.remove(info);
        }
    }

    @Override
    public void forgetOutput(IRecipe recipe, ItemInstance instance){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);
        if(information != null){
            information.knownOutputs.remove(instance);
        }
    }

    @Override
    public void forgetInformation(IResourceName name){
        this.information.remove(name);
    }
}
