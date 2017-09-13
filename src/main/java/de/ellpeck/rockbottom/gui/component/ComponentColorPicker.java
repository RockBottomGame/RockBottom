package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.tex.Texture;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.newdawn.slick.Graphics;

import java.util.function.BiConsumer;

public class ComponentColorPicker extends GuiComponent{

    private final Texture texture = AbstractGame.get().getAssetManager().getTexture(AbstractGame.internalRes("gui.colorpick"));

    private final BiConsumer<Integer, Boolean> consumer;
    private final boolean isEnlargable;
    private final int defX;
    private final int defY;
    private final int defSizeX;
    private final int defSizeY;
    private boolean wasMouseDown;
    private boolean isEnlarged;
    private int color;

    public ComponentColorPicker(Gui gui, int x, int y, int sizeX, int sizeY, int defaultColor, BiConsumer<Integer, Boolean> consumer, boolean isEnlargable){
        super(gui, x, y, sizeX, sizeY);
        this.consumer = consumer;
        this.color = defaultColor;
        this.isEnlargable = isEnlargable;

        this.defX = x;
        this.defY = y;
        this.defSizeX = sizeX;
        this.defSizeY = sizeY;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        this.texture.draw(this.x, this.y, this.sizeX, this.sizeY);

        g.setColor(this.colorOutline);
        g.drawRect(this.x, this.y, this.sizeX, this.sizeY);
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(this.isMouseOver(game)){
            if(Settings.KEY_GUI_ACTION_1.isKey(button)){
                if(this.isEnlargable && !this.isEnlarged){
                    this.sizeX *= 4;
                    this.sizeY *= 4;

                    this.x = Math.max(0, Math.min((int)game.getWidthInGui()-this.sizeX, this.x-(this.sizeX/2-(this.sizeX/8))));
                    this.y = Math.max(0, Math.min((int)game.getHeightInGui()-this.sizeY, this.y-(this.sizeY/2-(this.sizeY/8))));

                    this.isEnlarged = true;

                    this.gui.prioritize(this);
                }
                else if(!this.wasMouseDown){
                    this.consumer.accept(this.color, false);
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
            if(Settings.KEY_MENU.isKey(button)){
                this.unenlarge();
                return true;
            }
        }
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("color_picker");
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

            if(Settings.KEY_GUI_ACTION_1.isDown()){
                this.onClickOrMove(game, mouseX, mouseY);
            }
            else{
                this.consumer.accept(this.color, true);
                this.wasMouseDown = false;
            }
        }
    }

    private void onClickOrMove(IGameInstance game, float mouseX, float mouseY){
        if(this.isMouseOver(game)){
            float x = (mouseX-this.x)/this.sizeX*this.texture.getWidth();
            float y = (mouseY-this.y)/this.sizeY*this.texture.getHeight();
            int color = Colors.fromColor(this.texture.getColor((int)x, (int)y));

            if(this.color != color){
                this.color = color;
                this.consumer.accept(this.color, false);
            }
        }
    }
}
