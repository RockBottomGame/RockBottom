package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Keybind;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentScrollBar;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentKeybind;
import de.ellpeck.rockbottom.init.AbstractGame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiKeybinds extends Gui{

    public int activeKeybind = -1;
    private ComponentScrollBar scrollBar;
    private final ComponentKeybind[] keybinds = new ComponentKeybind[14];

    public GuiKeybinds(Gui parent){
        super(304, 150, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        int x = -102;
        int y = 0;
        for(int i = 0; i < this.keybinds.length; i++){
            this.keybinds[i] = new ComponentKeybind(this, 1+i, this.guiLeft+this.sizeX/2+x, this.guiTop+y);
            this.components.add(this.keybinds[i]);

            if((i+1)%2 == 0){
                x = -102;
                y += 18;
            }
            else{
                x = 0;
            }
        }

        BoundBox scrollArea = new BoundBox(this.sizeX/2-102, 0, this.sizeX/2+100, this.sizeX-26).add(this.guiLeft, this.guiTop);
        this.scrollBar = new ComponentScrollBar(this, this.guiLeft+this.sizeX/2-112, this.guiTop, 6, this.sizeY-26, 0, 0, RockBottomAPI.KEYBIND_REGISTRY.getSize()/2-7, scrollArea, (number) -> this.populateButtons());
        this.components.add(this.scrollBar);

        this.components.add(new ComponentButton(this, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(AbstractGame.internalRes("button.back"))));

        this.populateButtons();
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("keybinds");
    }

    private void populateButtons(){
        int offset = this.scrollBar.getNumber()*2;

        List<Keybind> binds = new ArrayList<>(RockBottomAPI.KEYBIND_REGISTRY.getUnmodifiable().values());
        binds.sort(Comparator.comparing(Keybind:: getName));

        for(int i = 0; i < this.keybinds.length; i++){
            ComponentKeybind comp = this.keybinds[i];

            if(binds.size() > offset+i){
                comp.setKeybind(binds.get(offset+i));
                comp.isVisible = true;
            }
            else{
                comp.isVisible = false;
            }
        }
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(!super.onMouseAction(game, button, x, y)){
            this.activeKeybind = -1;
        }
        return true;
    }
}
