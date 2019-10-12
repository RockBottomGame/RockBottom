package de.ellpeck.rockbottom.world.entity.player.knowledge;

import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.ConstructionRecipe;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.entity.player.knowledge.Information;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentEmpty;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.toast.IToast;
import de.ellpeck.rockbottom.api.toast.ToastBasic;
import de.ellpeck.rockbottom.api.toast.ToastItem;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Collections;
import java.util.List;

public class RecipeInformation extends Information {

    public static final ResourceName REG_NAME = ResourceName.intern("recipe");

    private PlayerCompendiumRecipe recipe;

    public RecipeInformation(PlayerCompendiumRecipe recipe) {
        super(recipe.getKnowledgeInformationName());
        this.recipe = recipe;
    }

    public RecipeInformation(ResourceName name) {
        super(name);
    }

    @Override
    public IToast announceForget() {
        return new ToastBasic(ResourceName.intern("gui.compendium.book_closed"), new ChatComponentTranslation(ResourceName.intern("info.recipe_forgotten")), this.getOutputName(), 200);
    }

    @Override
    public IToast announceTeach() {
        if (recipe == null) return new ToastBasic(ResourceName.intern("gui.compendium.book_open"), new ChatComponentTranslation(ResourceName.intern("info.recipe_learned")), this.getOutputName(), 200);
        return new ToastItem(recipe.getOutputs(), new ChatComponentTranslation(ResourceName.intern("info.recipe_learned")), Collections.singletonList(this.getOutputName()), 200);
    }

    private ChatComponent getOutputName() {
        if (this.recipe != null) {
            List<ItemInstance> outputs = this.recipe.getOutputs();
            ItemInstance output = outputs.get(0);
            return new ChatComponentText(output.getDisplayName() + " x" + output.getAmount());
        } else {
            return new ChatComponentEmpty();
        }
    }

    @Override
    public void save(DataSet set, IKnowledgeManager manager) {
        if (this.recipe != null) {
            set.addString("recipe_name", this.recipe.getName().toString());
        }
    }

    @Override
    public void load(DataSet set, IKnowledgeManager manager) {
        ResourceName recName = new ResourceName(set.getString("recipe_name"));
        this.recipe = ConstructionRecipe.forName(recName);
    }

    @Override
    public ResourceName getRegistryName() {
        return REG_NAME;
    }
}
