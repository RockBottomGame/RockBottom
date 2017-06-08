package de.ellpeck.rockbottom.api.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.gui.Gui;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;

public class ComponentInputField extends GuiComponent{

    private final boolean renderBox;
    private final boolean selectable;
    private final int maxLength;
    private final boolean displaxMaxLength;

    private String text = "";
    private boolean isActive;
    private int counter;

    public ComponentInputField(Gui gui, int x, int y, int sizeX, int sizeY, boolean renderBox, boolean selectable, boolean defaultActive, int maxLength, boolean displayMaxLength){
        super(gui, x, y, sizeX, sizeY);
        this.renderBox = renderBox;
        this.selectable = selectable;
        this.isActive = defaultActive;
        this.maxLength = maxLength;
        this.displaxMaxLength = displayMaxLength;
    }

    @Override
    public boolean onKeyboardAction(IGameInstance game, int button, char character){
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
            else{
                Input input = game.getContainer().getInput();
                if(input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyDown(Input.KEY_RCONTROL)){
                    if(button == Input.KEY_V){
                        if(this.text.length() < this.maxLength){
                            try{
                                this.text += (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                            }
                            catch(Exception ignored){
                            }

                            if(this.text.length() > this.maxLength){
                                this.text = this.text.substring(0, this.maxLength);
                            }

                            return true;
                        }
                    }
                }
                else if(!Character.isISOControl(character)){
                    if(this.text.length() < this.maxLength){
                        this.text += character;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void update(IGameInstance game){
        this.counter++;
    }

    public String getText(){
        return this.text;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        if(this.renderBox){
            g.setColor(this.isMouseOver(game) ? this.colorButton : this.colorButtonUnselected);
            g.fillRect(this.x, this.y, this.sizeX, this.sizeY);

            g.setColor(this.colorOutline);
            g.drawRect(this.x, this.y, this.sizeX, this.sizeY);
        }

        String text = this.text+(this.isActive ? ((this.counter/15)%2 == 0 ? "|" : " ") : "");

        Font font = manager.getFont();
        font.drawCutOffString(this.x+3, this.y+this.sizeY/2F-font.getHeight(0.35F)/2F, text, 0.35F, this.sizeX-6, true, false);

        if(this.displaxMaxLength){
            int diff = this.maxLength-this.text.length();
            FormattingCode format = diff <= 0 ? FormattingCode.RED : (diff <= this.maxLength/8 ? FormattingCode.ORANGE : (diff <= this.maxLength/4 ? FormattingCode.YELLOW : FormattingCode.NONE));
            font.drawStringFromRight(this.x+this.sizeX-1, this.y+this.sizeY-font.getHeight(0.2F), format.toString()+this.text.length()+"/"+this.maxLength, 0.2F);
        }
    }

    public void setText(String text){
        this.text = text;
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(button == game.getSettings().buttonGuiAction1){
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
        else if(button == game.getSettings().buttonGuiAction2){
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
