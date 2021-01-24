package de.ellpeck.rockbottom.construction.category;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.CompendiumCategory;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Set;

public class ManualConstructionCategory extends CompendiumCategory {

    public static final ManualConstructionCategory INSTANCE = new ManualConstructionCategory();

    public ManualConstructionCategory() {
        super(ResourceName.intern("manual_construction"));
    }

    @Override
    public ResourceName getIcon(IGameInstance game, IAssetManager assetManager, IRenderer g) {
        return this.getName().addPrefix("gui.compendium.");
    }

    @Override
    public Set<? extends ICompendiumRecipe> getRecipes() {
        return Registries.MANUAL_CONSTRUCTION_RECIPES.values();
    }
}
