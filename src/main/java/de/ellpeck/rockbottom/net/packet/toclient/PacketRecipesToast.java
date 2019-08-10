package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.construction.ConstructionRecipe;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.entity.player.knowledge.Information;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.toast.IToast;
import de.ellpeck.rockbottom.api.toast.ToastItem;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.entity.player.knowledge.KnowledgeManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class PacketRecipesToast implements IPacket {
    private List<ICompendiumRecipe> recipes = new ArrayList<>();

    public PacketRecipesToast(List<ICompendiumRecipe> recipes) {
        this.recipes = recipes;
    }

    public PacketRecipesToast() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        DataSet allRecipes = new DataSet();
        allRecipes.addInt("length", this.recipes.size());
        for (int i = 0; i < this.recipes.size(); i++) {
            ICompendiumRecipe recipe = this.recipes.get(i);
            DataSet recipeSet = new DataSet();
            String nameStr = recipe.getName().toString();
            recipeSet.addString("name", nameStr);
            if (recipe instanceof ConstructionRecipe) {
                recipeSet.addString("type", ((ConstructionRecipe)recipe).usesTools() ? "construction_table" : "manual");
            } else {
                recipeSet.addString("type", "unsupported");
            }
            allRecipes.addDataSet("recipe_" + i, recipeSet);
        }
        NetUtil.writeSetToBuffer(allRecipes, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        DataSet allRecipes = new DataSet();
        NetUtil.readSetFromBuffer(allRecipes, buf);
        int length = allRecipes.getInt("length");
        for (int i = 0; i < length; i++) {
            DataSet recipeSet = allRecipes.getDataSet("recipe_" + i);
            String nameStr = recipeSet.getString("name");
            ResourceName name = new ResourceName(nameStr);
            ConstructionRecipe recipe;
            String type = recipeSet.getString("type");
            if (type.equals("unsupported")) continue;
            else if (type.equals("construction_table")) recipe = Registries.CONSTRUCTION_TABLE_RECIPES.get(name);
            else recipe = Registries.MANUAL_CONSTRUCTION_RECIPES.get(name);
            this.recipes.add(recipe);
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        AbstractEntityPlayer player = game.getPlayer();
        if (player == null || recipes == null || recipes.size() == 0) return;

        List<ItemInstance> recipeItems = new ArrayList<>();
        List<ChatComponent> descriptions = new ArrayList<>();
        for (ICompendiumRecipe recipe : recipes) {
            ItemInstance item = recipe.getOutputs().get(0);
            recipeItems.add(item);
            descriptions.add(new ChatComponentText(item.getDisplayName() + " x" + item.getAmount()));
        }

        IToast toast = new ToastItem(recipeItems, new ChatComponentTranslation(ResourceName.intern(recipes.size() > 1 ? "info.recipes_learned" : "info.recipe_learned")), descriptions, recipes.size() > 1 ? (Constants.TARGET_TPS * recipes.size()) : 200);
        RockBottomAPI.getGame().getToaster().displayToast(toast);
    }
}
