package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Keybind;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentMenu;
import de.ellpeck.rockbottom.api.gui.component.ComponentText;
import de.ellpeck.rockbottom.api.gui.component.MenuComponent;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentKeybind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiKeybinds extends Gui{

    public int selectedKeybind = -1;

    public GuiKeybinds(Gui parent){
        super(304, 150, parent);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        BoundBox box = new BoundBox(this.width/2-104, 0, this.width/2+98, this.height-26).add(this.getX(), this.getY());
        ComponentMenu menu = new ComponentMenu(this, this.width/2-112, 0, this.height-26, 1, 7, box);
        this.components.add(menu);

        this.components.add(new ComponentButton(this, this.width/2-40, this.height-16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.back"))));

        List<Keybind> binds = new ArrayList<>(RockBottomAPI.KEYBIND_REGISTRY.values());
        binds.sort(Comparator.comparing(Keybind:: getName));

        int id = 0;
        for(Keybind bind : binds){
            menu.add(new MenuComponent(202, 16)
                    .add(0, 0, new ComponentText(this, 0, 0, 100, 16, 0.35F, true, game.getAssetManager().localize(bind.getName().addPrefix("key."))+": "))
                    .add(102, 0, new ComponentKeybind(this, id, 0, 0, bind)));
            id++;
        }

        menu.organize();
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("keybinds");
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(!super.onMouseAction(game, button, x, y)){
            this.selectedKeybind = -1;
        }
        return true;
    }
}
