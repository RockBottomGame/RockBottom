package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IInputHandler;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.*;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.util.Pair;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
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

public class InputHandler implements IInputHandler {

    private final RockBottom game;

    private final List<Integer> keysToProcess = new ArrayList<>();
    private final List<Integer> mouseInputsToProcess = new ArrayList<>();
    private final List<Integer> charsToProcess = new ArrayList<>();

    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Pair<Integer, Long>> pressedKeysTimed = new HashSet<>();
    private final Set<Integer> pressedMouse = new HashSet<>();
    private final int[] nextMouseWheelDelta = new int[2];
    private final int[] mouseWheelDelta = new int[2];
    private boolean isMouseInWindow = true;
    private int mouseX;
    private int mouseY;
    private boolean allowKeyboardRepeats;
    private int forcedCrashTimer;

    public InputHandler(RockBottom game) {
        this.game = game;

        GLFW.glfwSetCursorPosCallback(game.getWindow(), new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                CursorPosEvent event = new CursorPosEvent(window, x, y);
                if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
                    InputHandler.this.mouseX = (int) event.x;
                    InputHandler.this.mouseY = (int) event.y;
                }
            }
        });
        GLFW.glfwSetScrollCallback(game.getWindow(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                ScrollEvent event = new ScrollEvent(window, xOffset, yOffset);
                if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
                    InputHandler.this.nextMouseWheelDelta[0] = (int) event.xOffset;
                    InputHandler.this.nextMouseWheelDelta[1] = (int) event.yOffset;
                }
            }
        });
        GLFW.glfwSetKeyCallback(game.getWindow(), new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                KeyEvent event = new KeyEvent(window, scancode, mods, action, key);
                if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
                    if (event.action == GLFW.GLFW_PRESS || (event.action == GLFW.GLFW_REPEAT && InputHandler.this.allowKeyboardRepeats)) {
                        InputHandler.this.keysToProcess.add(event.key);
                    }
                }
            }
        });
        GLFW.glfwSetCharCallback(game.getWindow(), new GLFWCharCallback() {
            @Override
            public void invoke(long window, int codepoint) {
                CharEvent event = new CharEvent(window, codepoint);
                if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
                    InputHandler.this.charsToProcess.add(event.codepoint);
                }
            }
        });
        GLFW.glfwSetMouseButtonCallback(game.getWindow(), new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                MouseEvent event = new MouseEvent(window, mods, action, button);
                if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
                    if (event.action == GLFW.GLFW_PRESS) {
                        InputHandler.this.mouseInputsToProcess.add(event.button);
                    }
                }
            }
        });
        GLFW.glfwSetCursorEnterCallback(game.getWindow(), new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                InputHandler.this.isMouseInWindow = entered;
            }
        });
    }

    public void update() {
        this.pressedKeys.clear();
        this.pressedMouse.clear();

        for (int i = 0; i < 2; i++) {
            this.mouseWheelDelta[i] = this.nextMouseWheelDelta[i];
            this.nextMouseWheelDelta[i] = 0;
        }

        for (int code : this.charsToProcess) {
            this.charInput(code, Character.toChars(code));
        }

        for (int key : this.keysToProcess) {
            if (!this.keyPressed(key)) {
                this.pressedKeys.add(key);
                this.pressedKeysTimed.add(Pair.of(key, System.currentTimeMillis()));
            }
        }

        for (int button : this.mouseInputsToProcess) {
            if (!this.mousePressed(button)) {
                this.pressedMouse.add(button);
            }
        }

        if (this.isKeyDown(GLFW.GLFW_KEY_F7)) {
            this.forcedCrashTimer++;
            if (this.forcedCrashTimer >= Constants.TARGET_TPS * 3) {
                throw new RuntimeException("You forced a crash!");
            }
        }

        this.keysToProcess.clear();
        this.charsToProcess.clear();
        this.mouseInputsToProcess.clear();
    }

    protected boolean mousePressed(int button) {
        return this.game.getInteractionManager().onMouseAction(this.game, button);
    }

    protected boolean keyPressed(int key) {
        if (this.game.getGuiManager().getGui() == null) {
            AbstractEntityPlayer player = this.game.getPlayer();
            if (!player.isDead()) {
                if (Settings.KEY_MENU.isKey(key)) {
                    this.game.openIngameMenu();
                    return true;
                } else if (Settings.KEY_INVENTORY.isKey(key)) {
                    player.openGuiContainer(new GuiInventory(player), player.getInvContainer());
                    return true;
                } else if (Settings.KEY_COMPENDIUM.isKey(key)) {
                    player.openGuiContainer(new GuiCompendium(player), player.getInvContainer());
                    return true;
                } else if (Settings.KEY_CHAT.isKey(key) || Settings.KEY_COMMAND.isKey(key)) {
                    if (RockBottomAPI.getNet().isActive() || Main.debugMode) {
                        this.game.getGuiManager().openGui(new GuiChat(Settings.KEY_COMMAND.isKey(key)));
                    } else {
                        this.game.getChatLog().displayMessage(new ChatComponentTranslation(ResourceName.intern("info.no_server")));
                    }
                    return true;
                }
            }
        }

        if (Main.debugMode) {
            Renderer renderer = this.game.renderer;

            if (key == GLFW.GLFW_KEY_F1) {
                renderer.isDebug = !renderer.isDebug;
                return true;
            } else if (key == GLFW.GLFW_KEY_F2) {
                renderer.isLineDebug = !renderer.isLineDebug;
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, renderer.isLineDebug ? GL11.GL_LINE : GL11.GL_FILL);
            } else if (key == GLFW.GLFW_KEY_F4) {
                renderer.isGuiDebug = !renderer.isGuiDebug;
                return true;
            } else if (key == GLFW.GLFW_KEY_F5) {
                renderer.isItemInfoDebug = !renderer.isItemInfoDebug;
                return true;
            } else if (key == GLFW.GLFW_KEY_F6) {
                renderer.isChunkBorderDebug = !renderer.isChunkBorderDebug;
                return true;
            } else if (key == GLFW.GLFW_KEY_F7) {
                renderer.isBiomeDebug = !renderer.isBiomeDebug;
                return true;
            } else if (key == GLFW.GLFW_KEY_F8) {
                renderer.isHeightDebug = !renderer.isHeightDebug;
                return true;
            } else if (key == GLFW.GLFW_KEY_F9) {
                renderer.isBoundBoxDebug = !renderer.isBoundBoxDebug;
                return true;
            } else if (key == GLFW.GLFW_KEY_F11) {
                renderer.isCullingDebug = !renderer.isCullingDebug;
                return true;
            }
        }

        if (key == GLFW.GLFW_KEY_F3) {
            this.game.assetManager.load();
            return true;
        } else if (Settings.KEY_SCREENSHOT.isKey(key)) {
            this.game.takeScreenshot();
            return true;
        }

        return this.game.getInteractionManager().onKeyPressed(this.game, key);
    }

    protected boolean charInput(int codePoint, char[] characters) {
        return this.game.getInteractionManager().onCharInput(this.game, codePoint, characters);
    }

    @Override
    public boolean isMouseInWindow() {
        return this.isMouseInWindow;
    }

    @Override
    public boolean isMouseDown(int button) {
        return GLFW.glfwGetMouseButton(this.game.getWindow(), button) == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean wasMousePressed(int button) {
        return this.pressedMouse.contains(button);
    }

    @Override
    public boolean isKeyDown(int key) {
        return GLFW.glfwGetKey(this.game.getWindow(), key) == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean wasKeyPressed(int key) {
        return this.pressedKeys.contains(key);
    }

    @Override
    public int getMouseWheelChange() {
        return this.mouseWheelDelta[1];
    }

    @Override
    public int getHorizontalMouseWheelChange() {
        return this.mouseWheelDelta[0];
    }

    @Override
    public int getMouseX() {
        return this.mouseX;
    }

    @Override
    public int getMouseY() {
        return this.mouseY;
    }

    @Override
    public void allowKeyboardEvents(boolean allow) {
        this.allowKeyboardRepeats = allow;
    }
}
