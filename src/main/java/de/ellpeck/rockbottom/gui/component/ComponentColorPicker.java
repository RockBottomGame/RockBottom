package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.function.BiConsumer;

public class ComponentColorPicker extends GuiComponent{

    private final ITexture texture = RockBottomAPI.getGame().getAssetManager().getTexture(RockBottomAPI.createInternalRes("gui.colorpick"));

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
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, int x, int y){
        this.texture.draw(x, y, this.width, this.height);
        g.drawRect(x, y, this.width, this.height, getElementOutlineColor());
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(this.isMouseOver(game)){
            if(Settings.KEY_GUI_ACTION_1.isKey(button)){
                if(this.isEnlargable && !this.isEnlarged){
                    this.width *= 4;
                    this.height *= 4;

                    this.x = Util.clamp(this.x-(this.width/2-(this.width/8)), 0, (int)game.getWidthInGui()-this.width);
                    this.y = Util.clamp(this.y-(this.height/2-(this.height/8)), 0, (int)game.getHeightInGui()-this.height);

                    this.isEnlarged = true;
                    this.gui.sortComponents();
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
            this.width = this.defSizeX;
            this.height = this.defSizeY;

            this.isEnlarged = false;
            this.gui.sortComponents();
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
            float x = (mouseX-this.getRenderX())/this.width*this.texture.getWidth();
            float y = (mouseY-this.getRenderY())/this.height*this.texture.getHeight();
            int color = this.texture.getTextureColor((int)x, (int)y);

            if(this.color != color){
                this.color = color;
                this.consumer.accept(this.color, false);
            }
        }
    }

    @Override
    public int getPriority(){
        return this.isEnlarged ? 1000 : super.getPriority();
    }
}
