package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.IInputHandler;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.gui.GuiInventory;
import de.ellpeck.rockbottom.init.RockBottom;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.util.*;

public class InputHandler implements IInputHandler{

    private final RockBottom game;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> pressedMouse = new HashSet<>();

    public InputHandler(RockBottom game){
        this.game = game;
    }

    @Override
    public boolean isMouseDown(int button){
        return Mouse.isButtonDown(button);
    }

    @Override
    public boolean wasMousePressed(int button){
        return this.pressedMouse.contains(button);
    }

    @Override
    public boolean isKeyDown(int key){
        return Keyboard.isKeyDown(key);
    }

    @Override
    public boolean wasKeyPressed(int key){
        return this.pressedKeys.contains(key);
    }

    @Override
    public int getMouseX(){
        return Mouse.getX();
    }

    @Override
    public int getMouseY(){
        return Display.getHeight()-Mouse.getY()-1;
    }

    public void update(){
        this.pressedMouse.clear();
        this.pressedKeys.clear();

        while(Keyboard.next()){
            char character = Keyboard.getEventCharacter();
            int key = Keyboard.getEventKey();

            if(Keyboard.getEventKeyState()){
                this.pressedKeys.add(key);
                this.keyPressed(key, character);
            }
        }

        while(Mouse.next()){
            int button = Mouse.getEventButton();
            if(button >= 0){
                if(Mouse.getEventButtonState()){
                    this.pressedMouse.add(button);
                    this.mousePressed(button);
                }
            }
        }
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

        if(key == Keyboard.KEY_F1){
            this.game.graphics.isDebug = !this.game.graphics.isDebug;
            return;
        }
        else if(key == Keyboard.KEY_F3){
            this.game.assetManager.load(this.game);
            this.game.assetManager.loadCursors();
            return;
        }
        else if(key == Keyboard.KEY_F4){
            this.game.graphics.isGuiDebug = !this.game.graphics.isGuiDebug;
            return;
        }
        else if(key == Keyboard.KEY_F5){
            this.game.graphics.isItemInfoDebug = !this.game.graphics.isItemInfoDebug;
            return;
        }
        else if(key == Keyboard.KEY_F6){
            this.game.graphics.isChunkBorderDebug = !this.game.graphics.isChunkBorderDebug;
            return;
        }
        else if(Settings.KEY_SCREENSHOT.isKey(key)){
            this.game.takeScreenshot();
            return;
        }

        this.game.getInteractionManager().onKeyboardAction(this.game, key, c);
    }
}
