package de.ellpeck.rockbottom.construction.category;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.CompendiumCategory;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Set;

public class CategoryConstructionTable extends CompendiumCategory {

    public CategoryConstructionTable() {
        super(ResourceName.intern("construction_table"));
    }

    @Override
    public ResourceName getIcon(IGameInstance game, IAssetManager assetManager, IRenderer g) {
        return this.getName().addPrefix("gui.compendium.");
    }

    @Override
    public Set<? extends ICompendiumRecipe> getRecipes() {
        return Registries.CONSTRUCTION_TABLE_RECIPES.values();
    }

    @Override
    public boolean shouldDisplay(AbstractEntityPlayer player) {
    	ICompendiumRecipe recipe = ICompendiumRecipe.getRecipe(GameContent.TILE_CONSTRUCTION_TABLE.getName());
        return recipe == null || recipe.isKnown(player);
    }
}
