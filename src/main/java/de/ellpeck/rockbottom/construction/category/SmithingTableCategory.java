package de.ellpeck.rockbottom.construction.category;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.CompendiumCategory;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.construction.RecipeCache;

import java.util.Set;

public class SmithingTableCategory extends ToolConstructionCategory {

    public SmithingTableCategory() {
        super(ResourceName.intern("smithing_table"));
    }

    @Override
    public ResourceName getIcon(IGameInstance game, IAssetManager assetManager, IRenderer g) {
        return this.getName().addPrefix("gui.compendium.");
    }

    @Override
    public Set<? extends ICompendiumRecipe> getRecipes() {
        return Registries.SMITHING_RECIPES.values();
    }

    @Override
    public boolean shouldDisplay(AbstractPlayerEntity player) {
        return RecipeCache.smithingTable == null || RecipeCache.smithingTable.isKnown(player);
    }
}
