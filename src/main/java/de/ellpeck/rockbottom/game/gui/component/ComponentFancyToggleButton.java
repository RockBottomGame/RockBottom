package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.gui.Gui;

public class ComponentFancyToggleButton extends ComponentFancyButton{

    private boolean isToggled;

    public ComponentFancyToggleButton(Gui gui, int id, int x, int y, int sizeX, int sizeY, boolean defaultState, String texture, String... hover){
        super(gui, id, x, y, sizeX, sizeY, texture, hover);
        this.isToggled = defaultState;
    }

    @Override
    protected String getTexture(){
        return this.texture+(this.isToggled ? "_toggled" : "");
    }

    @Override
    public boolean onPressed(IGameInstance game){
        this.isToggled = !this.isToggled;
        return false;
    }
}
