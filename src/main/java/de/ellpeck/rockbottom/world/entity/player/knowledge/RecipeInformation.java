package de.ellpeck.rockbottom.world.entity.player.knowledge;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.entity.player.knowledge.Information;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentEmpty;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.toast.Toast;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.List;

public class RecipeInformation extends Information{

    public static final IResourceName REG_NAME = RockBottomAPI.createInternalRes("recipe");

    private IRecipe recipe;

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
        return new Toast(RockBottomAPI.createInternalRes("gui.construction.book_closed"), new ChatComponentText("Recipe forgotten"), this.getOutputName(), 200);
    }

    @Override
    public Toast announceTeach(){
        return new Toast(RockBottomAPI.createInternalRes("gui.construction.book_open"), new ChatComponentText("Recipe learned"), this.getOutputName(), 200);
    }

    private ChatComponent getOutputName(){
        if(this.recipe != null){
            List<ItemInstance> outputs = this.recipe.getOutputs();
            ItemInstance output = outputs.get(0);
            return new ChatComponentText(output.getDisplayName()+" x"+output.getAmount());
        }
        else{
            return new ChatComponentEmpty();
        }
    }

    @Override
    public void save(DataSet set, IKnowledgeManager manager){
        if(this.recipe != null){
            set.addString("recipe_name", this.recipe.getName().toString());
        }
    }

    @Override
    public void load(DataSet set, IKnowledgeManager manager){
        IResourceName recName = RockBottomAPI.createRes(set.getString("recipe_name"));
        this.recipe = RockBottomAPI.ALL_CONSTRUCTION_RECIPES.get(recName);
    }

    @Override
    public IResourceName getRegistryName(){
        return REG_NAME;
    }
}
