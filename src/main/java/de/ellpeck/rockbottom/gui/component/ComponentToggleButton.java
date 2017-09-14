package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.function.Supplier;

public class ComponentToggleButton extends ComponentButton{

    private final String locKey;
    private boolean isToggled;

    public ComponentToggleButton(Gui gui, int x, int y, int sizeX, int sizeY, boolean defaultState, Supplier<Boolean> supplier, String locKey, String... hover){
        super(gui, x, y, sizeX, sizeY, supplier, null, hover);
        this.isToggled = defaultState;
        this.locKey = locKey;
    }

    @Override
    protected String getText(){
        String resource = this.locKey+(this.isToggled ? "_toggled" : "");
        return RockBottomAPI.getGame().getAssetManager().localize(RockBottomAPI.createInternalRes(resource));
    }

    @Override
    public boolean onPressed(IGameInstance game){
        this.isToggled = !this.isToggled;
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("toggle_button");
    }
}
