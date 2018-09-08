package de.ellpeck.rockbottom.construction.category;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.CompendiumCategory;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.construction.ComponentIngredient;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;

import java.util.List;
import java.util.Set;

public class CategorySmelting extends CompendiumCategory {

    public CategorySmelting() {
        super(ResourceName.intern("smelting"));
    }

    @Override
    public ResourceName getIcon(IGameInstance game, IAssetManager assetManager, IRenderer g) {
        return this.getName().addPrefix("gui.construction.");
    }

    @Override
    public ResourceName getBackgroundPicture(Gui gui, IAssetManager manager) {
        return ResourceName.intern("gui.construction.page_recipes_smelting");
    }

    @Override
    public int getMaxIngredientAmount(Gui gui, List<ComponentIngredient> ingredients) {
        return 1;
    }

    @Override
    public Pos2 getIngredientPosition(Gui gui, ComponentIngredient ingredient, int index) {
        return new Pos2(73 + 29, 64);
    }

    @Override
    public Set<? extends ICompendiumRecipe> getRecipes() {
        return Registries.SMELTING_REGISTRY.values();
    }

    @Override
    public boolean shouldDisplay(AbstractEntityPlayer player) {
        return ConstructionRegistry.simpleFurnace == null || ConstructionRegistry.simpleFurnace.isKnown(player);
    }
}
