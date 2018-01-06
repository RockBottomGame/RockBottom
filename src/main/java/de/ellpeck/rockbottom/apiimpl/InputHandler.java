package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.IInputHandler;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.gui.GuiInventory;
import de.ellpeck.rockbottom.init.RockBottom;
import org.lwjgl.glfw.*;

import java.util.HashSet;
import java.util.Set;

public class InputHandler implements IInputHandler{

    private final RockBottom game;

    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> pressedMouse = new HashSet<>();

    private boolean keyboardRepeats;
    private boolean isMouseInWindow = true;
    private int mouseX;
    private int mouseY;
    private int mouseWheel;

    public InputHandler(RockBottom game){
        this.game = game;

        GLFW.glfwSetCursorPosCallback(game.getWindow(), new GLFWCursorPosCallback(){
            @Override
            public void invoke(long window, double x, double y){
                InputHandler.this.mouseX = (int)x;
                InputHandler.this.mouseY = (int)y;
            }
        });
        GLFW.glfwSetScrollCallback(game.getWindow(), new GLFWScrollCallback(){
            @Override
            public void invoke(long window, double xOffset, double yOffset){
                game.enqueueAction((game, o) -> InputHandler.this.mouseWheel = (int)yOffset, null);
            }
        });
        GLFW.glfwSetKeyCallback(game.getWindow(), new GLFWKeyCallback(){
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods){
                if(action == GLFW.GLFW_PRESS || (InputHandler.this.keyboardRepeats && action == GLFW.GLFW_REPEAT)){
                    game.enqueueAction((game, o) -> {
                        InputHandler.this.pressedKeys.add(key);
                        InputHandler.this.keyPressed(key);
                    }, null);
                }
            }
        });
        GLFW.glfwSetCharCallback(game.getWindow(), new GLFWCharCallback(){
            @Override
            public void invoke(long window, int codepoint){
                game.enqueueAction((game, o) -> InputHandler.this.charInput(codepoint, Character.toChars(codepoint)), null);
            }
        });
        GLFW.glfwSetMouseButtonCallback(game.getWindow(), new GLFWMouseButtonCallback(){
            @Override
            public void invoke(long window, int button, int action, int mods){
                if(action == GLFW.GLFW_PRESS){
                    game.enqueueAction((game, o) -> {
                        InputHandler.this.pressedMouse.add(button);
                        InputHandler.this.mousePressed(button);
                    }, null);
                }
            }
        });
        GLFW.glfwSetCursorEnterCallback(game.getWindow(), new GLFWCursorEnterCallback(){
            @Override
            public void invoke(long window, boolean entered){
                InputHandler.this.isMouseInWindow = entered;
            }
        });
    }

    public void reset(){
        this.pressedKeys.clear();
        this.pressedMouse.clear();
        this.mouseWheel = 0;
    }

    protected void mousePressed(int button){
        this.game.getInteractionManager().onMouseAction(this.game, button);
    }

    protected void keyPressed(int key){
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

        this.game.getInteractionManager().onKeyPressed(this.game, key);
    }

    protected void charInput(int codePoint, char[] characters){
        this.game.getInteractionManager().onCharInput(this.game, codePoint, characters);
    }

    @Override
    public boolean isMouseInWindow(){
        return this.isMouseInWindow;
    }

    @Override
    public boolean isMouseDown(int button){
        return GLFW.glfwGetMouseButton(this.game.getWindow(), button) == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean wasMousePressed(int button){
        return this.pressedMouse.contains(button);
    }

    @Override
    public boolean isKeyDown(int key){
        return GLFW.glfwGetKey(this.game.getWindow(), key) == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean wasKeyPressed(int key){
        return this.pressedKeys.contains(key);
    }

    @Override
    public void setKeyboardRepeatEvents(boolean should){
        this.keyboardRepeats = should;
    }

    @Override
    public int getMouseWheelChange(){
        return this.mouseWheel;
    }

    @Override
    public int getMouseX(){
        return this.mouseX;
    }

    @Override
    public int getMouseY(){
        return this.mouseY;
    }
}
