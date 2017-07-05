package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ComponentColorPicker extends GuiComponent{

    private final Image image = AbstractGame.get().getAssetManager().getTexture(AbstractGame.internalRes("gui.colorpick"));

    private final ICallback callback;
    private final boolean isEnlargable;
    private boolean wasMouseDown;
    private boolean isEnlarged;

    private final int defX;
    private final int defY;
    private final int defSizeX;
    private final int defSizeY;

    private Color color;

    public ComponentColorPicker(Gui gui, int x, int y, int sizeX, int sizeY, Color defaultColor, ICallback callback, boolean isEnlargable){
        super(gui, x, y, sizeX, sizeY);
        this.callback = callback;
        this.color = defaultColor;
        this.isEnlargable = isEnlargable;

        this.defX = x;
        this.defY = y;
        this.defSizeX = sizeX;
        this.defSizeY = sizeY;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        if(!this.isEnlarged){
            this.render(g);
        }
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, Graphics g){
        if(this.isEnlarged){
            this.render(g);
        }
    }

    private void render(Graphics g){
        this.image.draw(this.x, this.y, this.sizeX, this.sizeY);

        g.setColor(this.colorOutline);
        g.drawRect(this.x, this.y, this.sizeX, this.sizeY);
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(this.isMouseOver(game)){
            if(button == game.getSettings().buttonGuiAction1){
                if(this.isEnlargable && !this.isEnlarged){
                    this.sizeX *= 4;
                    this.sizeY *= 4;

                    this.x = Math.max(0, Math.min((int)game.getWidthInGui()-this.sizeX, this.x-(this.sizeX/2-(this.sizeX/8))));
                    this.y = Math.max(0, Math.min((int)game.getHeightInGui()-this.sizeY, this.y-(this.sizeY/2-(this.sizeY/8))));

                    this.isEnlarged = true;

                    this.gui.prioritize(this);
                }
                else if(!this.wasMouseDown){
                    this.callback.onFirstClick(game.getMouseInGuiX(), game.getMouseInGuiY(), this.color);
                    this.wasMouseDown = true;
                }

                return true;
            }
        }
        else{
            if(this.isEnlarged){
                this.unenlarge();
            }
        }

        return false;
    }

    @Override
    public boolean onKeyboardAction(IGameInstance game, int button, char character){
        if(this.isEnlarged){
            if(button == game.getSettings().keyMenu.key){
                this.unenlarge();
            }
        }
        return false;
    }

    private void unenlarge(){
        if(this.isEnlarged){
            this.x = this.defX;
            this.y = this.defY;
            this.sizeX = this.defSizeX;
            this.sizeY = this.defSizeY;

            this.isEnlarged = false;
        }
    }

    @Override
    public void update(IGameInstance game){
        if(this.wasMouseDown){
            float mouseX = game.getMouseInGuiX();
            float mouseY = game.getMouseInGuiY();

            if(game.getInput().isMouseButtonDown(game.getSettings().buttonGuiAction1)){
                this.onClickOrMove(game, mouseX, mouseY);
            }
            else{
                this.callback.onLetGo(mouseX, mouseY, this.color);
                this.wasMouseDown = false;
            }
        }
    }

    private void onClickOrMove(IGameInstance game, float mouseX, float mouseY){
        if(this.isMouseOver(game)){
            float x = (mouseX-this.x)/this.sizeX*this.image.getWidth();
            float y = (mouseY-this.y)/this.sizeY*this.image.getHeight();
            Color color = this.image.getColor((int)x, (int)y);

            if(!this.color.equals(color)){
                this.color = color;

                this.callback.onChange(mouseX, mouseY, this.color);
            }
        }
    }

    public interface ICallback{

        default void onChange(float mouseX, float mouseY, Color color){

        }

        default void onFirstClick(float mouseX, float mouseY, Color color){

        }

        default void onLetGo(float mouseX, float mouseY, Color color){

        }
    }
}
