package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class ComponentFancyToggleButton extends ComponentFancyButton{

    private final IResourceName texToggled;
    private boolean isToggled;

    public ComponentFancyToggleButton(Gui gui, int id, int x, int y, int sizeX, int sizeY, boolean defaultState, IResourceName texture, String... hover){
        super(gui, id, x, y, sizeX, sizeY, texture, hover);
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
}
