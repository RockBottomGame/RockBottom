package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.menu.GuiMods;

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
        if(this.gui.getSelectedMod() != this.mod){
            this.gui.selectMod(this.mod);
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

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("mod_button");
    }
}
