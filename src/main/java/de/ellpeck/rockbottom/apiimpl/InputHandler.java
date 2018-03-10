package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IInputHandler;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.gui.GuiCompendium;
import de.ellpeck.rockbottom.gui.GuiInventory;
import de.ellpeck.rockbottom.init.RockBottom;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputHandler implements IInputHandler{

    private final RockBottom game;

    private final List<Integer> keysToProcess = new ArrayList<>();
    private final List<Integer> mouseInputsToProcess = new ArrayList<>();
    private final List<Integer> charsToProcess = new ArrayList<>();

    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> pressedMouse = new HashSet<>();

    private boolean isMouseInWindow = true;
    private int mouseX;
    private int mouseY;
    private final int[] nextMouseWheelDelta = new int[2];
    private final int[] mouseWheelDelta = new int[2];
    private boolean allowKeyboardRepeats;
    private int forcedCrashTimer;

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
                InputHandler.this.nextMouseWheelDelta[0] = (int)xOffset;
                InputHandler.this.nextMouseWheelDelta[1] = (int)yOffset;
            }
        });
        GLFW.glfwSetKeyCallback(game.getWindow(), new GLFWKeyCallback(){
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods){
                if(action == GLFW.GLFW_PRESS || (action == GLFW.GLFW_REPEAT && InputHandler.this.allowKeyboardRepeats)){
                    InputHandler.this.keysToProcess.add(key);
                }
            }
        });
        GLFW.glfwSetCharCallback(game.getWindow(), new GLFWCharCallback(){
            @Override
            public void invoke(long window, int codepoint){
                InputHandler.this.charsToProcess.add(codepoint);
            }
        });
        GLFW.glfwSetMouseButtonCallback(game.getWindow(), new GLFWMouseButtonCallback(){
            @Override
            public void invoke(long window, int button, int action, int mods){
                if(action == GLFW.GLFW_PRESS){
                    InputHandler.this.mouseInputsToProcess.add(button);
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

    public void update(){
        this.pressedKeys.clear();
        this.pressedMouse.clear();

        for(int i = 0; i < 2; i++){
            this.mouseWheelDelta[i] = this.nextMouseWheelDelta[i];
            this.nextMouseWheelDelta[i] = 0;
        }

        for(int code : this.charsToProcess){
            this.charInput(code, Character.toChars(code));
        }

        for(int key : this.keysToProcess){
            if(!this.keyPressed(key)){
                this.pressedKeys.add(key);
            }
        }

        for(int button : this.mouseInputsToProcess){
            if(!this.mousePressed(button)){
                this.pressedMouse.add(button);
            }
        }

        if(this.isKeyDown(GLFW.GLFW_KEY_F7)){
            this.forcedCrashTimer++;
            if(this.forcedCrashTimer >= Constants.TARGET_TPS*3){
                throw new RuntimeException("You forced a crash!");
            }
        }

        this.keysToProcess.clear();
        this.charsToProcess.clear();
        this.mouseInputsToProcess.clear();
    }

    protected boolean mousePressed(int button){
        return this.game.getInteractionManager().onMouseAction(this.game, button);
    }

    protected boolean keyPressed(int key){
        if(this.game.getGuiManager().getGui() == null){
            AbstractEntityPlayer player = this.game.getPlayer();
            if(Settings.KEY_MENU.isKey(key)){
                this.game.openIngameMenu();
                return true;
            }
            else if(Settings.KEY_INVENTORY.isKey(key)){
                player.openGuiContainer(new GuiInventory(player), player.getInvContainer());
                return true;
            }
            else if(Settings.KEY_COMPENDIUM.isKey(key)){
                player.openGuiContainer(new GuiCompendium(player), player.getInvContainer());
            }
            else if(Settings.KEY_CHAT.isKey(key)){
                if(RockBottomAPI.getNet().isActive()){
                    this.game.getGuiManager().openGui(new GuiChat());
                }
                else{
                    this.game.getChatLog().displayMessage(new ChatComponentTranslation(RockBottomAPI.createInternalRes("info.no_server")));
                }
                return true;
            }
        }

        if(key == GLFW.GLFW_KEY_F1){
            this.game.renderer.isDebug = !this.game.renderer.isDebug;
            return true;
        }
        else if(key == GLFW.GLFW_KEY_F2){
            this.game.renderer.isLineDebug = !this.game.renderer.isLineDebug;
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, this.game.renderer.isLineDebug ? GL11.GL_LINE : GL11.GL_FILL);
        }
        else if(key == GLFW.GLFW_KEY_F3){
            this.game.assetManager.load();
            return true;
        }
        else if(key == GLFW.GLFW_KEY_F4){
            this.game.renderer.isGuiDebug = !this.game.renderer.isGuiDebug;
            return true;
        }
        else if(key == GLFW.GLFW_KEY_F5){
            this.game.renderer.isItemInfoDebug = !this.game.renderer.isItemInfoDebug;
            return true;
        }
        else if(key == GLFW.GLFW_KEY_F6){
            this.game.renderer.isChunkBorderDebug = !this.game.renderer.isChunkBorderDebug;
            return true;
        }
        else if(Settings.KEY_SCREENSHOT.isKey(key)){
            this.game.takeScreenshot();
            return true;
        }

        return this.game.getInteractionManager().onKeyPressed(this.game, key);
    }

    protected boolean charInput(int codePoint, char[] characters){
        return this.game.getInteractionManager().onCharInput(this.game, codePoint, characters);
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
    public int getMouseWheelChange(){
        return this.mouseWheelDelta[1];
    }

    @Override
    public int getHorizontalMouseWheelChange(){
        return this.mouseWheelDelta[0];
    }

    @Override
    public int getMouseX(){
        return this.mouseX;
    }

    @Override
    public int getMouseY(){
        return this.mouseY;
    }

    @Override
    public void allowKeyboardEvents(boolean allow){
        this.allowKeyboardRepeats = allow;
    }
}
