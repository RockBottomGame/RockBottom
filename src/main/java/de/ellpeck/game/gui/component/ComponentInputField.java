package de.ellpeck.game.gui.component;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.assets.font.Font;
import de.ellpeck.game.gui.Gui;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class ComponentInputField extends GuiComponent{

    private final boolean renderBox;
    private final boolean centered;
    private final boolean selectable;

    private String text = "";
    private boolean isActive;
    private int counter;

    public ComponentInputField(Gui gui, int x, int y, int sizeX, int sizeY, boolean renderBox, boolean centered, boolean selectable, boolean defaultActive){
        super(gui, x, y, sizeX, sizeY);
        this.renderBox = renderBox;
        this.centered = centered;
        this.selectable = selectable;
        this.isActive = defaultActive;
    }

    @Override
    public boolean onKeyboardAction(Game game, int button, char character){
        if(this.isActive){
            if(button == Input.KEY_BACK){
                if(!this.text.isEmpty()){
                    this.text = this.text.substring(0, this.text.length()-1);
                }
                return true;
            }
            else if(button == Input.KEY_ESCAPE){
                if(this.selectable){
                    this.isActive = false;
                    return true;
                }
            }
            else if(!Character.isISOControl(character)){
                this.text += character;
            }
        }
        return false;
    }

    @Override
    public void update(Game game){
        this.counter++;
    }

    public String getText(){
        return this.text;
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g){
        if(this.renderBox){
            g.setColor(this.isMouseOver(game) ? ComponentButton.COLOR : ComponentButton.COLOR_UNSELECTED);
            g.fillRoundRect(this.x, this.y, this.sizeX, this.sizeY, 2);

            g.setColor(Color.black);
            g.drawRoundRect(this.x, this.y, this.sizeX, this.sizeY, 2);
        }

        String text = this.text+(this.isActive ? ((this.counter/15)%2 == 0 ? "|" : " ") : "");

        Font font = manager.getFont();
        if(this.centered){
            font.drawCenteredString(this.x+this.sizeX/2F, this.y+this.sizeY/2F, text, 0.35F, true);
        }
        else{
            font.drawString(this.x+3, this.y+this.sizeY/2F-font.getHeight(0.35F)/2F, text, 0.35F);
        }
    }

    public void setText(String text){
        this.text = text;
    }

    @Override
    public boolean onMouseAction(Game game, int button, float x, float y){
        if(button == game.settings.buttonGuiAction1){
            if(this.selectable){
                if(this.isMouseOver(game)){
                    this.isActive = true;
                    return true;
                }
                else{
                    this.isActive = false;
                }
            }
        }
        else if(button == game.settings.buttonGuiAction2){
            if(this.isMouseOver(game)){
                this.text = "";

                if(this.selectable){
                    this.isActive = true;
                }

                return true;
            }
        }
        return false;
    }
}
