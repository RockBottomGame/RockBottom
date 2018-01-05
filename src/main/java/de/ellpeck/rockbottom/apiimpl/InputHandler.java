package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.IInputHandler;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.gui.GuiInventory;
import de.ellpeck.rockbottom.init.RockBottom;
import org.lwjgl.glfw.GLFW;

public class InputHandler implements IInputHandler{

    private final RockBottom game;

    public InputHandler(RockBottom game){
        this.game = game;
    }

    public void update(){
        //TODO Update keys
    }

    protected void mousePressed(int button){
        this.game.getInteractionManager().onMouseAction(this.game, button);
    }

    protected void keyPressed(int key, char c){
        if(this.game.getGuiManager().getGui() == null){
            if(Settings.KEY_MENU.isKey(key)){
                this.game.openIngameMenu();
                return;
            }
            else if(Settings.KEY_INVENTORY.isKey(key)){
                AbstractEntityPlayer player = this.game.getPlayer();
                player.openGuiContainer(new GuiInventory(player), player.getInvContainer());
                return;
            }
            else if(Settings.KEY_CHAT.isKey(key) && RockBottomAPI.getNet().isActive()){
                this.game.getGuiManager().openGui(new GuiChat());
                return;
            }
        }

        if(key == GLFW.GLFW_KEY_F1){
            this.game.renderer.isDebug = !this.game.renderer.isDebug;
            return;
        }
        else if(key == GLFW.GLFW_KEY_F3){
            this.game.assetManager.load(this.game);
            this.game.renderer.init();
            this.game.assetManager.loadCursors();
            return;
        }
        else if(key == GLFW.GLFW_KEY_F4){
            this.game.renderer.isGuiDebug = !this.game.renderer.isGuiDebug;
            return;
        }
        else if(key == GLFW.GLFW_KEY_F5){
            this.game.renderer.isItemInfoDebug = !this.game.renderer.isItemInfoDebug;
            return;
        }
        else if(key == GLFW.GLFW_KEY_F6){
            this.game.renderer.isChunkBorderDebug = !this.game.renderer.isChunkBorderDebug;
            return;
        }
        else if(Settings.KEY_SCREENSHOT.isKey(key)){
            this.game.takeScreenshot();
            return;
        }

        this.game.getInteractionManager().onKeyboardAction(this.game, key, c);
    }

    //TODO All this

    @Override
    public boolean isMouseInWindow(){
        return false;
    }

    @Override
    public boolean isMouseDown(int button){
        return false;
    }

    @Override
    public boolean wasMousePressed(int button){
        return false;
    }

    @Override
    public boolean isKeyDown(int key){
        return false;
    }

    @Override
    public boolean wasKeyPressed(int key){
        return false;
    }

    @Override
    public void setKeyboardRepeatEvents(boolean should){

    }

    @Override
    public int getMouseWheel(){
        return 0;
    }

    @Override
    public int getMouseX(){
        return 0;
    }

    @Override
    public int getMouseY(){
        return 0;
    }
}
