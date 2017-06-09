package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.api.gui.Gui;

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
        return RockBottom.get().getAssetManager().localize(RockBottom.internalRes(this.locKey+(this.isToggled ? "_toggled" : "")));
    }

    @Override
    public boolean onPressed(IGameInstance game){
        this.isToggled = !this.isToggled;
        return false;
    }
}
