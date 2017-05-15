package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.font.Font;
import de.ellpeck.rockbottom.gui.component.GuiComponent;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gui{

    public static final Color GRADIENT = new Color(0F, 0F, 0F, 0.5F);
    private static final Color HOVER_INFO_BACKGROUND = new Color(0F, 0F, 0F, 0.8F);
    protected final Gui parent;
    public int sizeX;
    public int sizeY;

    public int guiLeft;
    public int guiTop;
    protected List<GuiComponent> components = new ArrayList<>();

    public Gui(int sizeX, int sizeY){
        this(sizeX, sizeY, null);
    }

    public Gui(int sizeX, int sizeY, Gui parent){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.parent = parent;
    }

    public static void drawHoverInfoAtMouse(RockBottom game, AssetManager manager, Graphics g, boolean firstLineOffset, int maxLength, String... text){
        drawHoverInfoAtMouse(game, manager, g, firstLineOffset, maxLength, Arrays.asList(text));
    }

    public static void drawHoverInfoAtMouse(RockBottom game, AssetManager manager, Graphics g, boolean firstLineOffset, int maxLength, List<String> text){
        float mouseX = game.getMouseInGuiX();
        float mouseY = game.getMouseInGuiY();

        drawHoverInfo(game, manager, g, mouseX+18F/game.settings.guiScale, mouseY+18F/game.settings.guiScale, 0.25F, firstLineOffset, false, maxLength, text);
    }

    public static void drawHoverInfo(RockBottom game, AssetManager manager, Graphics g, float x, float y, float scale, boolean firstLineOffset, boolean canLeaveScreen, int maxLength, List<String> text){
        Font font = manager.getFont();

        float boxWidth = 0F;
        float boxHeight = 0F;

        if(maxLength > 0){
            text = font.splitTextToLength(maxLength, scale, true, text);
        }

        for(String s : text){
            float length = font.getWidth(s, scale);
            if(length > boxWidth){
                boxWidth = length;
            }

            if(firstLineOffset && boxHeight == 0F && text.size() > 1){
                boxHeight += 3F;
            }
            boxHeight += font.getHeight(scale);
        }

        if(boxWidth > 0F && boxHeight > 0F){
            boxWidth += 4F;
            boxHeight += 4F;

            if(!canLeaveScreen){
                x = Math.max(0, Math.min(x, (float)game.getWidthInGui()-boxWidth));
                y = Math.max(0, Math.min(y, (float)game.getHeightInGui()-boxHeight));
            }

            g.setColor(HOVER_INFO_BACKGROUND);
            g.fillRect(x, y, boxWidth, boxHeight);

            g.setColor(Color.black);
            g.drawRect(x, y, boxWidth, boxHeight);

            float yOffset = 0F;
            for(String s : text){
                font.drawString(x+2F, y+2F+yOffset, s, scale);

                if(firstLineOffset && yOffset == 0F){
                    yOffset += 3F;
                }
                yOffset += font.getHeight(scale);
            }
        }
    }

    public static void drawScaledImage(Graphics g, Image image, float x, float y, float scale, Color color){
        g.pushTransform();
        g.scale(scale, scale);
        image.draw(x/scale, y/scale, color);
        g.popTransform();
    }

    public void onOpened(RockBottom game){

    }

    public void onClosed(RockBottom game){

    }

    public void initGui(RockBottom game){
        if(!this.components.isEmpty()){
            this.components.clear();
        }

        this.initGuiVars(game);
    }

    protected void initGuiVars(RockBottom game){
        this.guiLeft = (int)game.getWidthInGui()/2-this.sizeX/2;
        this.guiTop = (int)game.getHeightInGui()/2-this.sizeY/2;
    }

    public void update(RockBottom game){
        this.components.forEach(component -> component.update(game));
    }

    public boolean onMouseAction(RockBottom game, int button, float x, float y){
        for(GuiComponent component : this.components){
            if(component.onMouseAction(game, button, x, y)){
                return true;
            }
        }
        return false;
    }

    public boolean onKeyboardAction(RockBottom game, int button, char character){
        for(GuiComponent component : this.components){
            if(component.onKeyboardAction(game, button, character)){
                return true;
            }
        }

        if(button == game.settings.keyMenu.key || (button == game.settings.keyInventory.key && this instanceof GuiContainer)){
            if(this.tryEscape(game)){
                return true;
            }
        }

        return false;
    }

    public void render(RockBottom game, AssetManager manager, Graphics g){
        this.components.forEach(component -> component.render(game, manager, g));
    }

    public void renderOverlay(RockBottom game, AssetManager manager, Graphics g){
        this.components.forEach(component -> component.renderOverlay(game, manager, g));
    }

    protected boolean tryEscape(RockBottom game){
        if(this.parent != null){
            game.guiManager.openGui(this.parent);
        }
        else{
            game.guiManager.closeGui();
        }

        return true;
    }

    public boolean doesPauseGame(){
        return true;
    }

    public boolean isMouseOverComponent(RockBottom game){
        return this.components.stream().anyMatch(component -> component.isMouseOver(game));
    }

    public boolean isMouseOver(RockBottom game){
        if(Mouse.isInsideWindow()){
            int mouseX = (int)game.getMouseInGuiX();
            int mouseY = (int)game.getMouseInGuiY();

            boolean overSelf = mouseX >= this.guiLeft && mouseX < this.guiLeft+this.sizeX && mouseY >= this.guiTop && mouseY < this.guiTop+this.sizeY;
            return overSelf || this.isMouseOverComponent(game);
        }
        else{
            return false;
        }
    }

    public boolean onButtonActivated(RockBottom game, int button){
        return false;
    }

    public boolean hasGradient(){
        return true;
    }
}
