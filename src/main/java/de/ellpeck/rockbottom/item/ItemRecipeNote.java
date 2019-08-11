package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.data.set.ModBasedDataSet;
import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.data.set.part.PartBoolean;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.construction.RecipeCache;

import java.util.List;
import java.util.Map;

public class ItemRecipeNote extends ItemBasic {
    public ItemRecipeNote() {
        super(ResourceName.intern("recipe_note"));
    }

    public static ItemInstance create(ICompendiumRecipe... recipes) {
        ItemInstance instance = new ItemInstance(GameContent.ITEM_RECIPE_NOTE);
        ModBasedDataSet set = new ModBasedDataSet();
        for (ICompendiumRecipe recipe : recipes) {
            set.addBoolean(recipe.getName(), true);
        }
        instance.getOrCreateAdditionalData().addModBasedDataSet(ResourceName.intern("recipes"), set);
        return instance;
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced, boolean isRealItem) {
        super.describeItem(manager, instance, desc, isAdvanced, isRealItem);
        desc.add(FormattingCode.GRAY + manager.localize(ResourceName.intern("info.recipe_note")));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance) {
        if (!world.isClient()) {
            ModBasedDataSet set = instance.getAdditionalData();
            if (set != null) {
                ModBasedDataSet recipes = set.getModBasedDataSet(ResourceName.intern("recipes"));
                for (Map.Entry<String, DataPart> entry : recipes) {
                    if (entry.getValue() instanceof PartBoolean) {
                        if (Util.isResourceName(entry.getKey())) {
                            ResourceName name = new ResourceName(entry.getKey());
                            PlayerCompendiumRecipe recipe = RecipeCache.getPlayerRecipe(name);
                            if (recipe != null) {
                                if (((PartBoolean) entry.getValue()).get()) {
                                    player.getKnowledge().teachRecipe(recipe);
                                } else {
                                    player.getKnowledge().forgetRecipe(recipe);
                                }
                            }
                        }
                    }
                }
            }
            player.getInv().remove(player.getSelectedSlot(), 1);
        }
        return true;
    }
}
