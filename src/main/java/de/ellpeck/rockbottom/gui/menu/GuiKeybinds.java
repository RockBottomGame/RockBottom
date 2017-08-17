package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Keybind;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentScrollMenu;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentKeybind;
import de.ellpeck.rockbottom.init.AbstractGame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiKeybinds extends Gui{

    public int activeKeybind = -1;

    public GuiKeybinds(Gui parent){
        super(304, 150, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        BoundBox box = new BoundBox(this.sizeX/2-104, 0, this.sizeX/2+98, this.sizeX-26).add(this.guiLeft, this.guiTop);
        ComponentScrollMenu menu = new ComponentScrollMenu(this, this.guiLeft+this.sizeX/2-112, this.guiTop, this.sizeY-26, 2, 7, box);
        this.components.add(menu);

        this.components.add(new ComponentButton(this, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(AbstractGame.internalRes("button.back"))));

        List<Keybind> binds = new ArrayList<>(RockBottomAPI.KEYBIND_REGISTRY.getUnmodifiable().values());
        binds.sort(Comparator.comparing(Keybind:: getName));

        int id = 0;
        for(Keybind bind : binds){
            menu.add(new ComponentKeybind(this, id, 0, 0, bind));
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
            this.activeKeybind = -1;
        }
        return true;
    }
}
