package de.ellpeck.game.gui;

import de.ellpeck.game.Constants;
import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.component.GuiComponent;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.util.ArrayList;
import java.util.List;

public class Gui{

    private static final Color HOVER_INFO_BACKGROUND = new Color(0F, 0F, 0F, 0.7F);
    public static final Color GRADIENT = new Color(0F, 0F, 0F, 0.25F);

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

    public void renderOverlay(Game game, AssetManager manager, Graphics g){
        this.components.forEach(component -> component.renderOverlay(game, manager, g));
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
        int mouseX = input.getMouseX()/game.settings.guiScale;
        int mouseY = input.getMouseY()/game.settings.guiScale;

        boolean overSelf = mouseX >= this.guiLeft && mouseX < this.guiLeft+this.sizeX && mouseY >= this.guiTop && mouseY < this.guiTop+this.sizeY;
        return overSelf || this.isMouseOverComponent(game);
    }

    public static void drawHoverInfoAtMouse(Game game, Graphics g, Color color, String... text){
        Input input = game.getContainer().getInput();
        float mouseX = (float)input.getMouseX()/(float)game.settings.guiScale;
        float mouseY = (float)input.getMouseY()/(float)game.settings.guiScale;

        drawHoverInfo(game, g, mouseX+3, mouseY+3, 0.25F, color, text);
    }

    public static void drawHoverInfo(Game game, Graphics g, float x, float y, float scale, Color color, String... text){
        Font font = game.getContainer().getDefaultFont();

        float boxWidth = 0F;
        float boxHeight = text.length*font.getLineHeight()*scale;

        for(String s : text){
            int length = font.getWidth(s);
            if(length > boxWidth){
                boxWidth = length*scale;
            }
        }

        if(boxWidth > 0F && boxHeight > 0F){
            g.setColor(HOVER_INFO_BACKGROUND);
            g.fillRoundRect(x, y, boxWidth+4, boxHeight+4, 2);

            g.setColor(color);

            int yOffset = 0;
            for(String s : text){
                drawScaledText(game, g, x+2, y+2+yOffset, scale, s);
                yOffset += font.getLineHeight();
            }
        }
    }

    public static void drawScaledText(Game game, Graphics g, float x, float y, float scale, String s){
        g.pushTransform();
        g.scale(scale, scale);
        game.getContainer().getDefaultFont().drawString(x*1/scale, y*1/scale, s, Color.white);
        g.popTransform();
    }
}
