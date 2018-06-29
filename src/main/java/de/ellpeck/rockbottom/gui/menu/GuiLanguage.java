package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.Locale;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentMenu;
import de.ellpeck.rockbottom.api.gui.component.MenuComponent;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Map;

public class GuiLanguage extends Gui {

    public GuiLanguage(Gui parent) {
        super(150, 150, parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        BoundBox area = new BoundBox(0, 0, 150, 106).add(this.getX(), this.getY());
        ComponentMenu menu = new ComponentMenu(this, -8, 0, 106, 1, 6, area);
        this.components.add(menu);

        IAssetManager manager = game.getAssetManager();
        for (Map.Entry<ResourceName, Locale> entry : manager.<Locale>getAllOfType(Locale.ID).entrySet()) {
            ResourceName res = entry.getKey();
            Locale loc = entry.getValue();

            menu.add(new MenuComponent(150, 16).add(0, 0, new ComponentButton(this, 0, 0, 150, 16, () -> {
                if (manager.getLocale() != loc) {
                    game.getSettings().currentLocale = res.toString();
                    game.getSettings().save();

                    manager.setLocale(loc);
                    game.getGuiManager().updateDimensions();
                    return true;
                }
                return false;
            }, loc.localize(null, res.addPrefix("loc.")))));
        }

        menu.organize();

        this.components.add(new ComponentButton(this, this.width / 2 - 40, this.height - 16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.back"))));
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("language");
    }
}
