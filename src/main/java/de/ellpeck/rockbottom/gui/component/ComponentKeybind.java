package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Keybind;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.menu.GuiKeybinds;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Input;

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
        String name = this.bind.isMouse() ? Mouse.getButtonName(this.bind.getKey()) : Input.getKeyName(this.bind.getKey());
        return this.isSelected() ? "<?>" : name;
    }

    @Override
    public boolean onKeyboardAction(IGameInstance game, int button, char character){
        if(this.isSelected()){
            this.bind.setBind(button, false);
            this.gui.selectedKeybind = -1;

            return true;
        }
        else{
            return super.onKeyboardAction(game, button, character);
        }
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(this.isSelected()){
            this.bind.setBind(button, true);
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
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("keybind");
    }
}
