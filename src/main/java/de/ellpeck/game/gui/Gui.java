package de.ellpeck.game.gui;

import de.ellpeck.game.Constants;
import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.component.GuiComponent;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.util.ArrayList;
import java.util.List;

public class Gui{

    public static final Color GRADIENT = new Color(0F, 0F, 0F, 0.35F);

    protected List<GuiComponent> components = new ArrayList<>();

    public final int sizeX;
    public final int sizeY;

    public int guiLeft;
    public int guiTop;

    protected final EntityPlayer player;

    public Gui(EntityPlayer player, int sizeX, int sizeY){
        this.player = player;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public void onClosed(Game game){

    }

    public void initGui(Game game){
        this.components.clear();

        this.guiLeft = (int)game.getWidthInGui()/2-this.sizeX/2;
        this.guiTop = (int)game.getHeightInGui()/2-this.sizeY/2;
    }

    public void update(Game game){
        this.components.forEach(component -> component.update(game));
    }

    public boolean onMouseAction(Game game, int button){
        for(GuiComponent component : this.components){
            if(component.onMouseAction(game, button)){
                return true;
            }
        }
        return false;
    }

    public boolean onKeyboardAction(Game game, int button){
        if(this.canEscape() && (button == Keyboard.KEY_ESCAPE || button == Keyboard.KEY_E)){
            this.player.guiManager.closeGui();
            return true;
        }
        else{
            for(GuiComponent component : this.components){
                if(component.onKeyboardAction(game, button)){
                    return true;
                }
            }
            return false;
        }
    }

    public void render(Game game, AssetManager manager, Graphics g){
        this.components.forEach(component -> component.render(game, manager, g));
    }

    protected boolean canEscape(){
        return true;
    }

    public boolean doesPauseGame(){
        return true;
    }

    public boolean isMouseOverComponent(Game game){
        return this.components.stream().anyMatch(component -> component.isMouseOver(game));
    }

    public boolean isMouseOver(Game game){
        Input input = game.getContainer().getInput();
        int mouseX = input.getMouseX()/Constants.GUI_SCALE;
        int mouseY = input.getMouseY()/Constants.GUI_SCALE;

        boolean overSelf = mouseX >= this.guiLeft && mouseX < this.guiLeft+this.sizeX && mouseY >= this.guiTop && mouseY < this.guiTop+this.sizeY;
        return overSelf || this.isMouseOverComponent(game);
    }
}
