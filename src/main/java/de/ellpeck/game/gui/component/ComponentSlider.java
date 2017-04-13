package de.ellpeck.game.gui.component;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.Gui;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ComponentSlider extends ComponentButton{

    protected final ICallback callback;
    protected final int min;
    protected final int max;
    protected int number;

    private boolean wasMouseDown;

    public ComponentSlider(Gui gui, int id, int x, int y, int sizeX, int sizeY, int initialNumber, int min, int max, ICallback callback, String text, String... hover){
        super(gui, id, x, y, sizeX, sizeY, text, hover);
        this.min = min;
        this.max = max;
        this.number = initialNumber;
        this.callback = callback;
    }

    @Override
    protected String getText(){
        return super.getText()+": "+this.number;
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        float percentage = (float)(this.number-this.min)/(float)(this.max-this.min);
        float x = this.x+percentage*(this.sizeX-5);

        g.setColor(this.isMouseOver(game) ? COLOR : COLOR_UNSELECTED);
        g.fillRect(x, this.y, 5F, this.sizeY);

        g.setColor(Color.black);
        g.drawRect(x, this.y, 5F, this.sizeY);
    }

    @Override
    public void update(Game game){
        boolean isMouseDown = game.getContainer().getInput().isMouseButtonDown(game.settings.buttonGuiAction1);
        float mouseX = game.getMouseInGuiX();
        float mouseY = game.getMouseInGuiY();

        if(isMouseDown){
            if(this.wasMouseDown || this.isMouseOver(game)){
                this.onClickOrMove(mouseX, mouseY);

                if(!this.wasMouseDown){
                    this.callback.onFirstClick(mouseX, mouseY, this.min, this.max, this.number);
                    this.wasMouseDown = true;
                }
            }
        }
        else if(this.wasMouseDown){
            this.callback.onLetGo(mouseX, mouseY, this.min, this.max, this.number);
            this.wasMouseDown = false;
        }
    }

    private void onClickOrMove(float mouseX, float mouseY){
        float clickPercentage = (mouseX-this.x)/(float)this.sizeX;
        int number = Math.max(this.min, Math.min(this.max, (int)(clickPercentage*(this.max-this.min+1)+this.min)));

        if(number != this.number){
            this.number = number;

            this.callback.onNumberChange(mouseX, mouseY, this.min, this.max, this.number);
        }
    }

    public interface ICallback{

        default void onNumberChange(float mouseX, float mouseY, int min, int max, int number){

        }

        default void onFirstClick(float mouseX, float mouseY, int min, int max, int number){

        }

        default void onLetGo(float mouseX, float mouseY, int min, int max, int number){

        }
    }
}
