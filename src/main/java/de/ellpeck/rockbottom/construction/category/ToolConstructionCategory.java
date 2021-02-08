package de.ellpeck.rockbottom.construction.category;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.CompendiumCategory;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.MenuComponent;
import de.ellpeck.rockbottom.api.gui.component.construction.ConstructComponent;
import de.ellpeck.rockbottom.api.gui.component.construction.IngredientComponent;
import de.ellpeck.rockbottom.api.gui.component.construction.PolaroidComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.List;

public abstract class ToolConstructionCategory extends CompendiumCategory {

    public ToolConstructionCategory(ResourceName name) {
        super(name);
    }

    @Override
    public void onGuiOrganized(Gui gui, MenuComponent menu, List<PolaroidComponent> polaroids, List<IngredientComponent> ingredients, ConstructComponent construct) {
        super.onGuiOrganized(gui, menu, polaroids, ingredients, construct);
    }

    @Override
    public ResourceName getBackgroundPicture(Gui gui, IAssetManager manager) {
        return ResourceName.intern("gui.compendium.page_recipes_tools");
    }
}
