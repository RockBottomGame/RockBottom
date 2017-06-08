package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.gui.Gui;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class GuiComponent{

    public final Color guiColor = RockBottom.get().settings.guiColor;
    public final Color colorButton = this.guiColor.multiply(new Color(1F, 1F, 1F, 0.5F));
    public final Color colorButtonUnselected = this.colorButton.darker(0.4F);
    public final Color colorOutline = this.guiColor.darker(0.3F);
    public final int sizeX;
    public final int sizeY;
    public Gui gui;
    public int x;
    public int y;

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
