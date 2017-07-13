package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.init.AbstractGame;

public class ComponentToggleButton extends ComponentButton{

    private final String locKey;
    private boolean isToggled;

    public ComponentToggleButton(Gui gui, int id, int x, int y, int sizeX, int sizeY, boolean defaultState, String locKey, String... hover){
        super(gui, id, x, y, sizeX, sizeY, null, hover);
        this.isToggled = defaultState;
        this.locKey = locKey;
    }

    @Override
    protected String getText(){
        return AbstractGame.get().getAssetManager().localize(AbstractGame.internalRes(this.locKey+(this.isToggled ? "_toggled" : "")));
    }

    @Override
    public boolean onPressed(IGameInstance game){
        this.isToggled = !this.isToggled;
        return false;
    }
}
