package de.ellpeck.rockbottom.world.entity.player.knowledge;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.entity.player.knowledge.Information;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.toast.Toast;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeInformation extends Information{

    private static final IResourceName REG_NAME = RockBottomAPI.createInternalRes("recipe");

    public final Set<IUseInfo> knownInputs = new HashSet<>();
    public final Set<ItemInstance> knownOutputs = new HashSet<>();
    public IRecipe recipe;

    public RecipeInformation(IRecipe recipe){
        super(getInfoName(recipe));
        this.recipe = recipe;
    }

    public RecipeInformation(IResourceName name){
        super(name);
    }

    public static IResourceName getInfoName(IRecipe recipe){
        return recipe.getName().addPrefix("recipe_");
    }

    @Override
    public Toast announceForget(){
        return new Toast(RockBottomAPI.createInternalRes("gui.construction_toggled"), new ChatComponentText("Recipe forgotten"), this.getOutputName(), 200);
    }

    @Override
    public Toast announceTeach(){
        return new Toast(RockBottomAPI.createInternalRes("gui.construction"), new ChatComponentText("Recipe learned"), this.getOutputName(), 200);
    }

    private ChatComponent getOutputName(){
        List<ItemInstance> outputs = this.recipe.getOutputs();
        ItemInstance output = outputs.get(0);

        if(this.knownOutputs.contains(output)){
            return new ChatComponentText(output.getDisplayName()+" x"+output.getAmount());
        }
        else{
            return new ChatComponentText("??? x"+output.getAmount());
        }
    }

    @Override
    public void save(DataSet set, IKnowledgeManager manager){
        set.addString("recipe_name", this.recipe.getName().toString());

        int inputCounter = 0;
        for(IUseInfo info : this.knownInputs){
            set.addInt("in_"+inputCounter, this.recipe.getInputs().indexOf(info));
            inputCounter++;
        }
        set.addInt("in_amount", inputCounter);

        int outputCounter = 0;
        for(ItemInstance instance : this.knownOutputs){
            set.addInt("out_"+outputCounter, this.recipe.getOutputs().indexOf(instance));
            outputCounter++;
        }
        set.addInt("out_amount", outputCounter);
    }

    @Override
    public void load(DataSet set, IKnowledgeManager manager){
        IResourceName recName = RockBottomAPI.createRes(set.getString("recipe_name"));
        this.recipe = RockBottomAPI.ALL_CONSTRUCTION_RECIPES.get(recName);

        if(this.recipe != null){
            int inputAmount = set.getInt("in_amount");
            for(int j = 0; j < inputAmount; j++){
                List<IUseInfo> inputs = this.recipe.getInputs();
                int input = set.getInt("in_"+j);

                if(input >= 0 && input < inputs.size()){
                    IUseInfo info = inputs.get(input);
                    if(info != null){
                        this.knownInputs.add(info);
                    }
                }
            }

            int outputAmount = set.getInt("out_amount");
            for(int j = 0; j < outputAmount; j++){
                List<ItemInstance> outputs = this.recipe.getOutputs();
                int output = set.getInt("out_"+j);

                if(output >= 0 && output < outputs.size()){
                    ItemInstance instance = outputs.get(output);
                    if(instance != null){
                        this.knownOutputs.add(instance);
                    }
                }
            }
        }
        else{
            IResourceName name = this.getName();
            RockBottomAPI.logger().warning("Couldn't load recipe information "+name+" because recipe with name "+recName+" is missing!");
            manager.forgetInformation(name);
        }
    }

    @Override
    public IResourceName getRegistryName(){
        return REG_NAME;
    }
}
