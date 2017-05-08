package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.Gui;
import org.newdawn.slick.Graphics;

public class ComponentButton extends GuiComponent{

    public final int id;
    protected final String text;
    private final String[] hover;

    public boolean hasBackground = true;

    public ComponentButton(Gui gui, int id, int x, int y, int sizeX, int sizeY, String text, String... hover){
        super(gui, x, y, sizeX, sizeY);
        this.id = id;
        this.text = text;
        this.hover = hover;
    }

    public ComponentButton setHasBackground(boolean has){
        this.hasBackground = has;
        return this;
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        if(this.hasBackground){
            g.setColor(this.isMouseOver(game) ? this.colorButton : this.colorButtonUnselected);
            g.fillRect(this.x, this.y, this.sizeX, this.sizeY);

            g.setColor(this.colorOutline);
            g.drawRect(this.x, this.y, this.sizeX, this.sizeY);
        }

        String text = this.getText();
        if(text != null){
            manager.getFont().drawCenteredString(this.x+this.sizeX/2F, this.y+this.sizeY/2F+0.5F, text, 0.35F, true);
        }
    }

    protected String getText(){
        return this.text;
    }

    protected String[] getHover(){
        return this.hover;
    }

    @Override
    public void renderOverlay(RockBottom game, AssetManager manager, Graphics g){
        if(this.isMouseOver(game)){
            String[] hover = this.getHover();
            if(hover != null && hover.length > 0){
                Gui.drawHoverInfoAtMouse(game, manager, g, false, 100, hover);
            }
        }
    }

    @Override
    public boolean onMouseAction(RockBottom game, int button, float x, float y){
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

    public boolean onPressed(RockBottom game){
        return false;
    }
}
