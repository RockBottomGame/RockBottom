package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.CompendiumCategory;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.GuiCompendium;

public class ComponentCompendiumCategory extends GuiComponent {

    private final CompendiumCategory category;
    private final boolean isActive;
    private final GuiCompendium gui;

    public ComponentCompendiumCategory(GuiCompendium gui, int x, int y, CompendiumCategory category, boolean isActive) {
        super(gui, x, y, 16, 14);
        this.gui = gui;
        this.category = category;
        this.isActive = isActive;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
        ResourceName texture = ResourceName.intern("gui.construction." + (this.isActive ? "tab_extended" : "tab"));
        manager.getTexture(texture).draw(x, y, this.width, this.height);

        int theX = this.isActive ? 3 : 1;
        manager.getTexture(this.category.getIcon(game, manager, g)).draw(x + theX, y + 1, 12, 12);
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
        if (this.isMouseOver(game)) {
            g.drawHoverInfoAtMouse(game, manager, false, 0, this.category.getDisplayText(manager));
        }
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
        if (this.isMouseOverPrioritized(game)) {
            if (!this.isActive) {
                Gui gui = this.category.getGuiOverride(this.gui);
                if (gui == null) {
                    gui = new GuiCompendium(game.getPlayer(), this.category);
                }

                this.gui.keepContainerOpen = true;
                game.getGuiManager().openGui(gui);

                return true;
            }
        }
        return false;
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("compendium_category");
    }
}
