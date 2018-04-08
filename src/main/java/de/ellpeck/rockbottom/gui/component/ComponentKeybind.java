package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.settings.Keybind;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.menu.GuiKeybinds;

public class ComponentKeybind extends ComponentButton{

    private final GuiKeybinds gui;
    private final Keybind bind;
    private final int id;

    public ComponentKeybind(GuiKeybinds gui, int id, int x, int y, Keybind bind){
        super(gui, x, y, 100, 16, null, null);
        this.gui = gui;
        this.id = id;
        this.bind = bind;
    }

    @Override
    protected String getText(){
        return this.isSelected() ? "<?>" : this.bind.getDisplayName();
    }

    @Override
    public boolean onKeyPressed(IGameInstance game, int button){
        if(this.isSelected()){
            this.bind.setBind(button);
            this.gui.selectedKeybind = -1;

            return true;
        }
        else{
            return super.onKeyPressed(game, button);
        }
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(this.isSelected()){
            this.bind.setBind(button);
            this.gui.selectedKeybind = -1;

            return true;
        }
        else{
            return super.onMouseAction(game, button, x, y);
        }
    }

    @Override
    public boolean onPressed(IGameInstance game){
        if(!this.isSelected()){
            this.gui.selectedKeybind = this.id;
            return true;
        }
        return false;
    }

    public boolean isSelected(){
        return this.gui.selectedKeybind == this.id;
    }

    @Override
    public ResourceName getName(){
        return ResourceName.intern("keybind");
    }
}
