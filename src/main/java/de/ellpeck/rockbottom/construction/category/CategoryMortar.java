package de.ellpeck.rockbottom.construction.category;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.CompendiumCategory;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;

import java.util.Set;

public class CategoryMortar extends CompendiumCategory {

    public CategoryMortar() {
        super(ResourceName.intern("mortar"));
    }

    @Override
    public ResourceName getIcon(IGameInstance game, IAssetManager assetManager, IRenderer g) {
        return this.getName().addPrefix("gui.construction.");
    }

    @Override
    public Set<? extends ICompendiumRecipe> getRecipes() {
        return Registries.MORTAR_REGISTRY.values();
    }

    @Override
    public boolean shouldDisplay(AbstractEntityPlayer player) {
        if (ConstructionRegistry.mortar == null) {
            return true;
        } else {
            return ConstructionRegistry.mortar.isKnown(player);
        }
    }
}
