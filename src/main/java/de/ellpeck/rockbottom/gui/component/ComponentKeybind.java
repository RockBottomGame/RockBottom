package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Keybind;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.menu.GuiKeybinds;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Input;

public class ComponentKeybind extends ComponentButton{

    private final GuiKeybinds gui;
    private Keybind bind;
    private final int id;

    public ComponentKeybind(GuiKeybinds gui, int id, int x, int y){
        super(gui, x, y, 100, 16, null, null);
        this.gui = gui;
        this.id = id;
    }

    public void setKeybind(Keybind bind){
        this.bind = bind;
    }

    @Override
    protected String getText(){
        IAssetManager manager = AbstractGame.get().getAssetManager();
        String name = this.bind.isMouse() ? Mouse.getButtonName(this.bind.getKey()) : Input.getKeyName(this.bind.getKey());
        return manager.localize(this.bind.getName().addPrefix("key."))+": "+(this.isActive() ? "<?>" : name);
    }

    @Override
    public boolean onKeyboardAction(IGameInstance game, int button, char character){
        if(this.isActive()){
            this.bind.setBind(button, false);
            this.gui.activeKeybind = -1;

            return true;
        }
        else{
            return super.onKeyboardAction(game, button, character);
        }
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(this.isActive()){
            this.bind.setBind(button, true);
            this.gui.activeKeybind = -1;

            return true;
        }
        else{
            return super.onMouseAction(game, button, x, y);
        }
    }

    @Override
    public boolean onPressed(IGameInstance game){
        if(!this.isActive()){
            this.gui.activeKeybind = this.id;
            return true;
        }
        return false;
    }

    private boolean isActive(){
        return this.gui.activeKeybind == this.id;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("keybind");
    }
}
