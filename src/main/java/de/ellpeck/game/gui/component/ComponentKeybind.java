package de.ellpeck.game.gui.component;

import de.ellpeck.game.Game;
import de.ellpeck.game.Settings;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.menu.GuiKeybinds;
import org.newdawn.slick.Input;

public class ComponentKeybind extends ComponentButton{

    private final GuiKeybinds gui;
    private final Settings.Keybind bind;

    public ComponentKeybind(GuiKeybinds gui, int id, int x, int y, Settings.Keybind bind){
        super(gui, id, x, y, 150, 16, null);
        this.gui = gui;
        this.bind = bind;
    }

    @Override
    protected String getText(){
        AssetManager manager = Game.get().assetManager;
        return manager.localize("key."+this.bind.name)+": "+(this.isActive() ? "<?>" : Input.getKeyName(this.bind.key));
    }

    @Override
    public boolean onKeyboardAction(Game game, int button){
        if(this.isActive()){
            if(this.bind.key != button){
                this.bind.key = button;
                this.gui.change = true;
            }
            this.gui.activeKeybind = -1;

            return true;
        }
        else{
            return super.onKeyboardAction(game, button);
        }
    }

    @Override
    public boolean onPressed(Game game){
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
