package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TranslationChatComponent;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.toast.IToast;
import de.ellpeck.rockbottom.api.toast.ItemToast;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class RecipesToastPacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("recipes_toast");
    private List<PlayerCompendiumRecipe> recipes = new ArrayList<>();

    public RecipesToastPacket(List<PlayerCompendiumRecipe> recipes) {
        this.recipes = recipes;
    }

    public RecipesToastPacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        DataSet allRecipes = new DataSet();
        allRecipes.addInt("length", this.recipes.size());
        for (int i = 0; i < this.recipes.size(); i++) {
            PlayerCompendiumRecipe recipe = this.recipes.get(i);
            allRecipes.addString("recipe_" + i, recipe.getName().toString());
        }
        NetUtil.writeSetToBuffer(allRecipes, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        DataSet allRecipes = new DataSet();
        NetUtil.readSetFromBuffer(allRecipes, buf);
        int length = allRecipes.getInt("length");
        for (int i = 0; i < length; i++) {
            ResourceName name = new ResourceName(allRecipes.getString("recipe_" + i));
            ICompendiumRecipe recipe = Registries.ALL_RECIPES.get(name);
            if (recipe instanceof PlayerCompendiumRecipe) recipes.add((PlayerCompendiumRecipe)recipe);
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        AbstractPlayerEntity player = game.getPlayer();
        if (player == null || recipes == null || recipes.size() == 0) return;

        List<ItemInstance> recipeItems = new ArrayList<>();
        List<ChatComponent> descriptions = new ArrayList<>();
        for (PlayerCompendiumRecipe recipe : recipes) {
            ItemInstance item = recipe.getOutputs().get(0);
            recipeItems.add(item);
            descriptions.add(new TextChatComponent(item.getDisplayName() + " x" + item.getAmount()));
        }

        IToast toast = new ItemToast(recipeItems, new TranslationChatComponent(ResourceName.intern(recipes.size() > 1 ? "info.recipes_learned" : "info.recipe_learned")), descriptions, recipes.size() > 1 ? (Constants.TARGET_TPS * recipes.size()) : 200);
        RockBottomAPI.getGame().getToaster().displayToast(toast);
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
