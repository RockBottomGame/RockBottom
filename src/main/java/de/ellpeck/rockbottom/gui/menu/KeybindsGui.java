package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.data.settings.Keybind;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.gui.component.MenuComponent;
import de.ellpeck.rockbottom.api.gui.component.TextComponent;
import de.ellpeck.rockbottom.api.gui.component.MenuItemComponent;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.component.KeybindComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KeybindsGui extends Gui {

    public int selectedKeybind = -1;

    public KeybindsGui(Gui parent) {
        super(304, 150, parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        BoundingBox box = new BoundingBox(this.width / 2 - 104, 0, this.width / 2 + 98, this.height - 26).add(this.getX(), this.getY());
        MenuComponent menu = new MenuComponent(this, this.width / 2 - 112, 0, this.height - 26, 1, 7, box);
        this.components.add(menu);

        this.components.add(new ButtonComponent(this, this.width / 2 - 40, this.height - 16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.back"))));

        List<Keybind> binds = new ArrayList<>(Registries.KEYBIND_REGISTRY.values());
        binds.sort(Comparator.comparing(Keybind::getName));

        int id = 0;
        for (Keybind bind : binds) {
            menu.add(new MenuItemComponent(202, 16)
                    .add(0, 0, new TextComponent(this, 0, 0, 100, 16, 0.35F, true, game.getAssetManager().localize(bind.getName().addPrefix("key.")) + ": "))
                    .add(102, 0, new KeybindComponent(this, id, 0, 0, bind)));
            id++;
        }

        menu.organize();
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("keybinds");
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
        if (!super.onMouseAction(game, button, x, y)) {
            this.selectedKeybind = -1;
        }
        return true;
    }
}
