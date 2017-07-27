package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.settings.Keybind;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.gui.menu.GuiKeybinds;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.newdawn.slick.Input;

public class ComponentKeybind extends ComponentButton{

    private final GuiKeybinds gui;
    private Keybind bind;

    public ComponentKeybind(GuiKeybinds gui, int id, int x, int y){
        super(gui, id, x, y, 100, 16, null);
        this.gui = gui;
    }

    public void setKeybind(Keybind bind){
        this.bind = bind;
    }

    @Override
    protected String getText(){
        IAssetManager manager = AbstractGame.get().getAssetManager();
        return manager.localize(this.bind.getName().addPrefix("key."))+": "+(this.isActive() ? "<?>" : Input.getKeyName(this.bind.getKey()));
    }

    @Override
    public boolean onKeyboardAction(IGameInstance game, int button, char character){
        if(this.isActive()){
            this.bind.setBind(button);
            this.gui.activeKeybind = -1;

            return true;
        }
        else{
            return super.onKeyboardAction(game, button, character);
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
}
