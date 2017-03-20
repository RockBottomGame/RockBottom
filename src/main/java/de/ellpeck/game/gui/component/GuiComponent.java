package de.ellpeck.game.gui.component;

import de.ellpeck.game.Constants;
import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class GuiComponent{

    public int x;
    public int y;

    public final int sizeX;
    public final int sizeY;

    public GuiComponent(int x, int y, int sizeX, int sizeY){
        this.x = x;
        this.y = y;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public void update(Game game){

    }

    public void render(Game game, AssetManager manager, Graphics g){

    }

    public boolean isMouseOver(Game game){
        Input input = game.getContainer().getInput();
        int mouseX = input.getMouseX()/Constants.GUI_SCALE;
        int mouseY = input.getMouseY()/Constants.GUI_SCALE;

        return mouseX >= this.x && mouseX < this.x+this.sizeX && mouseY >= this.y && mouseY < this.y+this.sizeY;
    }

    public boolean onMouseAction(Game game, int button){
        return false;
    }

    public boolean onKeyboardAction(Game game, int button){
        return false;
    }
}
