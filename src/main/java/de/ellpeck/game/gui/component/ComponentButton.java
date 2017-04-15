package de.ellpeck.game.gui.component;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.Gui;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ComponentButton extends GuiComponent{

    public static final Color COLOR = Gui.GUI_COLOR.multiply(new Color(1F, 1F, 1F, 0.5F));
    public static final Color COLOR_UNSELECTED = COLOR.darker(0.4F);

    protected final int id;
    protected final String text;
    private final String[] hover;

    public ComponentButton(Gui gui, int id, int x, int y, int sizeX, int sizeY, String text, String... hover){
        super(gui, x, y, sizeX, sizeY);
        this.id = id;
        this.text = text;
        this.hover = hover;
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g){
        g.setColor(this.isMouseOver(game) ? COLOR : COLOR_UNSELECTED);
        g.fillRoundRect(this.x, this.y, this.sizeX, this.sizeY, 2);

        g.setColor(Color.black);
        g.drawRoundRect(this.x, this.y, this.sizeX, this.sizeY, 2);

        String text = this.getText();
        if(text != null){
            manager.getFont().drawCenteredString(this.x+this.sizeX/2F, this.y+this.sizeY/2F, text, 0.35F, true);
        }
    }

    protected String getText(){
        return this.text;
    }

    protected String[] getHover(){
        return this.hover;
    }

    @Override
    public void renderOverlay(Game game, AssetManager manager, Graphics g){
        String[] hover = this.getHover();
        if(hover != null && hover.length > 0){
            if(this.isMouseOver(game)){
                Gui.drawHoverInfoAtMouse(game, manager, g, false, 100, hover);
            }
        }
    }

    @Override
    public boolean onMouseAction(Game game, int button, float x, float y){
        if(button == game.settings.buttonGuiAction1 && this.isMouseOver(game)){
            if(this.onPressed(game)){
                return true;
            }
            else if(this.gui != null && this.gui.onButtonActivated(game, this.id)){
                return true;
            }
        }
        return false;
    }

    public boolean onPressed(Game game){
        return false;
    }
}
