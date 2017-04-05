package de.ellpeck.game.gui;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.assets.font.Font;
import de.ellpeck.game.gui.component.GuiComponent;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.List;

public class Gui{

    public static final Color GUI_COLOR = new Color(0x529652);
    private static final Color HOVER_INFO_BACKGROUND = new Color(0F, 0F, 0F, 0.8F);
    public static final Color GRADIENT = new Color(0F, 0F, 0F, 0.25F);

    protected List<GuiComponent> components = new ArrayList<>();

    public final int sizeX;
    public final int sizeY;

    public int guiLeft;
    public int guiTop;

    protected final EntityPlayer player;

    protected final Gui parent;

    public Gui(EntityPlayer player, int sizeX, int sizeY){
        this(player, sizeX, sizeY, null);
    }

    public Gui(EntityPlayer player, int sizeX, int sizeY, Gui parent){
        this.player = player;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.parent = parent;
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
        if(button == game.settings.keyMenu.key || (button == game.settings.keyInventory.key && this instanceof GuiInventory)){
            if(this.tryEscape()){
                return true;
            }
        }

        for(GuiComponent component : this.components){
            if(component.onKeyboardAction(game, button)){
                return true;
            }
        }

        return false;
    }

    public void render(Game game, AssetManager manager, Graphics g){
        this.components.forEach(component -> component.render(game, manager, g));
    }

    public void renderOverlay(Game game, AssetManager manager, Graphics g){
        this.components.forEach(component -> component.renderOverlay(game, manager, g));
    }

    protected boolean tryEscape(){
        if(this.parent != null){
            this.player.guiManager.openGui(this.parent);
        }
        else{
            this.player.guiManager.closeGui();
        }

        return true;
    }

    public boolean doesPauseGame(){
        return true;
    }

    public boolean isMouseOverComponent(Game game){
        return this.components.stream().anyMatch(component -> component.isMouseOver(game));
    }

    public boolean isMouseOver(Game game){
        int mouseX = (int)game.getMouseInGuiX();
        int mouseY = (int)game.getMouseInGuiY();

        boolean overSelf = mouseX >= this.guiLeft && mouseX < this.guiLeft+this.sizeX && mouseY >= this.guiTop && mouseY < this.guiTop+this.sizeY;
        return overSelf || this.isMouseOverComponent(game);
    }

    public static void drawHoverInfoAtMouse(Game game, AssetManager manager, Graphics g, boolean firstLineOffset, List<String> text){
        drawHoverInfoAtMouse(game, manager, g, firstLineOffset, text.toArray(new String[text.size()]));
    }

    public static void drawHoverInfoAtMouse(Game game, AssetManager manager, Graphics g, boolean firstLineOffset, String... text){
        float mouseX = game.getMouseInGuiX();
        float mouseY = game.getMouseInGuiY();

        drawHoverInfo(game, manager, g, mouseX+3, mouseY+3, 0.25F, firstLineOffset, false, text);
    }

    public static void drawHoverInfo(Game game, AssetManager manager, Graphics g, float x, float y, float scale, boolean firstLineOffset, boolean canLeaveScreen, String... text){
        Font font = manager.getFont();

        float boxWidth = 0F;
        float boxHeight = 0F;

        for(String s : text){
            float length = font.getWidth(s, scale);
            if(length > boxWidth){
                boxWidth = length;
            }

            if(firstLineOffset && boxHeight == 0F){
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
            g.fillRoundRect(x, y, boxWidth, boxHeight, 2);

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

    public boolean onButtonActivated(Game game, int button){
        return false;
    }
}
