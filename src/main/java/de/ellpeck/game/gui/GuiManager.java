package de.ellpeck.game.gui;

import de.ellpeck.game.Game;

public class GuiManager{

    private Gui gui;

    public void update(Game game){
        if(this.gui != null){
            this.gui.update(game);
        }
    }

    public void openGui(Gui gui){
        Game game = Game.get();

        if(this.gui != null){
            this.gui.onClosed(game);
        }

        this.gui = gui;

        if(this.gui != null){
            this.gui.initGui(game);
        }
    }

    public void closeGui(){
        this.openGui(null);
    }

    public Gui getGui(){
        return this.gui;
    }

    public boolean onMouseAction(Game game, int button){
        return this.gui != null && this.gui.onMouseAction(game, button);
    }

    public boolean onKeyboardAction(Game game, int button){
        return this.gui != null && this.gui.onKeyboardAction(game, button);
    }
}