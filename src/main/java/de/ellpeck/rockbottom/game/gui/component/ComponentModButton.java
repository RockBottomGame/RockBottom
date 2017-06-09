package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.game.gui.menu.GuiMods;

public class ComponentModButton extends ComponentButton{

    private final GuiMods gui;
    private final IMod mod;

    public ComponentModButton(GuiMods gui, IMod mod, int id, int x, int y){
        super(gui, id, x, y, 100, 16, null);
        this.mod = mod;
        this.gui = gui;
    }

    @Override
    public boolean onPressed(IGameInstance game){
        if(this.gui.selectedMod != this.mod){
            this.gui.selectedMod = this.mod;
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    protected String getText(){
        return this.mod.getDisplayName();
    }
}
