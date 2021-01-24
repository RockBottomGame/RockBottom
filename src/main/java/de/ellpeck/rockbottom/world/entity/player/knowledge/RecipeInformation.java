package de.ellpeck.rockbottom.world.entity.player.knowledge;

import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.ConstructionRecipe;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.entity.player.knowledge.Information;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.EmptyChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TranslationChatComponent;
import de.ellpeck.rockbottom.api.toast.IToast;
import de.ellpeck.rockbottom.api.toast.BasicToast;
import de.ellpeck.rockbottom.api.toast.ItemToast;
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
        return new BasicToast(ResourceName.intern("gui.compendium.book_closed"), new TranslationChatComponent(ResourceName.intern("info.recipe_forgotten")), this.getOutputName(), 200);
    }

    @Override
    public IToast announceTeach() {
        if (recipe == null) return new BasicToast(ResourceName.intern("gui.compendium.book_open"), new TranslationChatComponent(ResourceName.intern("info.recipe_learned")), this.getOutputName(), 200);
        return new ItemToast(recipe.getOutputs(), new TranslationChatComponent(ResourceName.intern("info.recipe_learned")), Collections.singletonList(this.getOutputName()), 200);
    }

    private ChatComponent getOutputName() {
        if (this.recipe != null) {
            List<ItemInstance> outputs = this.recipe.getOutputs();
            ItemInstance output = outputs.get(0);
            return new TextChatComponent(output.getDisplayName() + " x" + output.getAmount());
        } else {
            return new EmptyChatComponent();
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
