package de.ellpeck.game.gui.component;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.Gui;
import org.newdawn.slick.Graphics;

public class GuiComponent{

    public Gui gui;

    public int x;
    public int y;

    public final int sizeX;
    public final int sizeY;

    public GuiComponent(Gui gui, int x, int y, int sizeX, int sizeY){
        this.gui = gui;
        this.x = x;
        this.y = y;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public void update(Game game){

    }

    public void render(Game game, AssetManager manager, Graphics g){

    }

    public void renderOverlay(Game game, AssetManager manager, Graphics g){

    }

    public boolean isMouseOver(Game game){
        int mouseX = (int)game.getMouseInGuiX();
        int mouseY = (int)game.getMouseInGuiY();

        return mouseX >= this.x && mouseX < this.x+this.sizeX && mouseY >= this.y && mouseY < this.y+this.sizeY;
    }

    public boolean onMouseAction(Game game, int button, float x, float y){
        return false;
    }

    public boolean onKeyboardAction(Game game, int button){
        return false;
    }
}
