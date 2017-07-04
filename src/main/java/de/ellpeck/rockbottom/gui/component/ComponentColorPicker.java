package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ComponentColorPicker extends GuiComponent{

    private final Image image = AbstractGame.get().getAssetManager().getTexture(AbstractGame.internalRes("gui.colorpick"));

    private final ICallback callback;
    private boolean wasMouseDown;

    private Color color;

    public ComponentColorPicker(Gui gui, int x, int y, int sizeX, int sizeY, Color defaultColor, ICallback callback){
        super(gui, x, y, sizeX, sizeY);
        this.callback = callback;
        this.color = defaultColor;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        this.image.draw(this.x, this.y, this.sizeX, this.sizeY);

        g.setColor(this.colorOutline);
        g.drawRect(this.x, this.y, this.sizeX, this.sizeY);
    }


    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(this.isMouseOver(game)){
            if(!this.wasMouseDown){
                this.callback.onFirstClick(game.getMouseInGuiX(), game.getMouseInGuiY(), this.color);
                this.wasMouseDown = true;

                return true;
            }
        }
        return false;
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
