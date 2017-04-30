package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.Gui;
import org.lwjgl.input.Mouse;
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

    public void update(RockBottom game){

    }

    public void render(RockBottom game, AssetManager manager, Graphics g){

    }

    public void renderOverlay(RockBottom game, AssetManager manager, Graphics g){

    }

    public boolean isMouseOver(RockBottom game){
        if(Mouse.isInsideWindow()){
            int mouseX = (int)game.getMouseInGuiX();
            int mouseY = (int)game.getMouseInGuiY();

            return mouseX >= this.x && mouseX < this.x+this.sizeX && mouseY >= this.y && mouseY < this.y+this.sizeY;
        }
        else{
            return false;
        }
    }

    public boolean onMouseAction(RockBottom game, int button, float x, float y){
        return false;
    }

    public boolean onKeyboardAction(RockBottom game, int button, char character){
        return false;
    }
}
