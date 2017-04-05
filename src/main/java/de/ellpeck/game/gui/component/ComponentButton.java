package de.ellpeck.game.gui.component;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.Gui;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class ComponentButton extends GuiComponent{

    private final String text;
    private final String[] hover;
    private final Runnable action;

    public ComponentButton(int x, int y, int sizeX, int sizeY, Runnable action, String text, String... hover){
        super(x, y, sizeX, sizeY);
        this.action = action;
        this.text = text;
        this.hover = hover;
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g){
        g.setColor(Gui.GUI_COLOR);
        g.fillRoundRect(this.x, this.y, this.sizeX, this.sizeY, 2);

        g.setColor(Color.black);
        g.drawRoundRect(this.x, this.y, this.sizeX, this.sizeY, 2);

        if(this.text != null){
            manager.getFont().drawCenteredString(this.x+this.sizeX/2, this.y+this.sizeY/2, this.text, 0.35F, true);
        }
    }

    @Override
    public void renderOverlay(Game game, AssetManager manager, Graphics g){
        if(this.hover != null && this.hover.length > 0){
            if(this.isMouseOver(game)){
                Gui.drawHoverInfoAtMouse(game, manager, g, false, this.hover);
            }
        }
    }

    @Override
    public boolean onMouseAction(Game game, int button){
        if(button == Input.MOUSE_LEFT_BUTTON && this.isMouseOver(game)){
            this.action.run();
            return true;
        }
        else{
            return false;
        }
    }
}
