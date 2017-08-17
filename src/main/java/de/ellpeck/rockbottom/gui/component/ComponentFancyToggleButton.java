package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.function.Supplier;

public class ComponentFancyToggleButton extends ComponentFancyButton{

    private final IResourceName texToggled;
    private boolean isToggled;

    public ComponentFancyToggleButton(Gui gui, int x, int y, int sizeX, int sizeY, boolean defaultState, Supplier<Boolean> supplier, IResourceName texture, String... hover){
        super(gui, x, y, sizeX, sizeY, supplier, texture, hover);
        this.isToggled = defaultState;
        this.texToggled = this.texture.addSuffix("_toggled");
    }

    @Override
    protected IResourceName getTexture(){
        return this.isToggled ? this.texToggled : this.texture;
    }

    @Override
    public boolean onPressed(IGameInstance game){
        this.isToggled = !this.isToggled;
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("fancy_toggle_button");
    }
}
