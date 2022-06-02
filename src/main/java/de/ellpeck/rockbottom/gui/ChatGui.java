package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.FormatSelectorComponent;
import de.ellpeck.rockbottom.api.gui.component.InputFieldComponent;
import de.ellpeck.rockbottom.api.gui.component.ScrollBarComponent;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.net.packet.toserver.SendChatPacket;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatGui extends Gui {

    private static final int BACKING_ONE = Colors.rgb(0F, 0F, 0F, 0.65F);
    private static final int BACKING_TWO = Colors.rgb(0.1F, 0.1F, 0.1F, 0.65F);
    public static boolean isSelectorOpen;
    private final List<String> suggestions = new ArrayList<>();
    private FormatSelectorComponent selector;
    private InputFieldComponent inputField;
    private ScrollBarComponent scrollBar;
    private int selectedSuggestion;
    private int selectedLastInput = -1;
    private float suggestionX;
    private final boolean startCommand;

    public ChatGui(boolean startCommand) {
        this.startCommand = startCommand;
    }

    public static int drawMessages(IGameInstance game, IAssetManager manager, IRenderer g, List<ChatComponent> messages, int messageCount, int offset, int maxHeight) {
        IFont font = manager.getFont();
        float scale = 0.25F;
        float fontHeight = font.getHeight(scale);
        int sizeX = (int) g.getWidthInGui() / 2;

        int yStart = (int) g.getHeightInGui() - 26 - (int) fontHeight;
        int yAdd = 0;

        boolean alternate = game.getChatLog().getMessages().size() % 2 == 0;

        int lineCounter = 0;
        outer:
        for (int j = 0; j < messageCount; j++) {
            ChatComponent message = messages.get(j);
            List<String> split = font.splitTextToLength(sizeX, scale, true, message.getDisplayWithChildren(game, manager));

            for (int i = split.size() - 1; i >= 0; i--) {
                String s = split.get(i);

                lineCounter++;
                if (lineCounter > offset) {
                    if (yAdd + fontHeight + 1 >= maxHeight) {
                        break outer;
                    }

                    g.addFilledRect(8, yStart - yAdd, sizeX, fontHeight + 1, alternate ? BACKING_ONE : BACKING_TWO);
                    font.drawString(9, yStart - yAdd + 1, s, scale);

                    yAdd += fontHeight + 1;
                }
            }

            alternate = !alternate;
        }

        return lineCounter;
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        IChatLog chat = game.getChatLog();
        this.inputField = new InputFieldComponent(this, 8, this.height - 21, this.width / 2 - 18, 16, true, false, true, 512, true, strg -> {
            this.suggestions.clear();
            this.suggestionX = 8 + Math.min(this.inputField.getWidth() - 6, game.getAssetManager().getFont().getWidth(strg, 0.35F));
            this.selectedSuggestion = 0;
            this.selectedLastInput = -1;

            if (strg.startsWith("/")) {
                String[] split = strg.substring(1).split(" ");
                if (split.length > 0) {
                    Command command = chat.getCommand(split[0]);
                    if (command != null) {
                        String[] args = Arrays.copyOfRange(split, 1, split.length);
                        int index = strg.endsWith(" ") ? args.length : args.length - 1;

                        for (String suggestion : command.getAutocompleteSuggestions(args, index, game.getPlayer(), game, game.getChatLog())) {
                            if (index == args.length || suggestion.contains(args[index])) {
                                this.suggestions.add(suggestion);
                            }
                        }

                        return;
                    }
                }

                for (Command command : Registries.COMMAND_REGISTRY.values()) {
                    for (String trigger : command.getTriggers()) {
                        if (split.length <= 0 || trigger.startsWith(split[0])) {
                            this.suggestions.add(trigger);
                        }
                    }
                }
            }
        });
        if (startCommand) {
            this.inputField.append("/");
        }
        this.components.add(this.inputField);

        this.selector = new FormatSelectorComponent(this, 7 + this.width / 2 - 16, this.height - 21, this.inputField);
        this.components.add(this.selector);
        if (isSelectorOpen) {
            this.selector.openMenu();
        }

        int height = this.height / 3 * 2;
        int y = (int) game.getRenderer().getHeightInGui() - 24 - height;
        this.scrollBar = new ScrollBarComponent(this, 1, y, height - 1, new BoundingBox(0, 0, game.getRenderer().getWidthInGui() / 2, height).add(8, y), 0, null);
        this.scrollBar.setDrawReversed(true);
        this.components.add(this.scrollBar);
    }

    @Override
    public void onClosed(IGameInstance game) {
        super.onClosed(game);
        isSelectorOpen = this.selector.isMenuOpen();
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        List<ChatComponent> messages = game.getChatLog().getMessages();
        int drawnLines = drawMessages(game, manager, g, messages, messages.size(), this.scrollBar.getNumber(), this.height / 3 * 2);
        this.scrollBar.setMax(drawnLines);

        if (!this.suggestions.isEmpty()) {
            IFont font = manager.getFont();
            float charHeight = font.getHeight(0.3F);

            float longestWidth = 0F;
            for (String sugg : this.suggestions) {
                float width = font.getWidth(sugg, 0.3F);
                if (width > longestWidth) {
                    longestWidth = width;
                }
            }

            int displayed = Math.min(20, this.suggestions.size() - 1);

            float x = this.suggestionX - 1;
            float y = this.height - 30 - displayed * charHeight - 1;
            float width = longestWidth + 1;
            float height = (displayed + 1) * charHeight + 1;

            g.addFilledRect(x, y, width, height, Gui.HOVER_INFO_BACKGROUND);
            g.addEmptyRect(x, y, width, height, Gui.GRADIENT_COLOR);

            for (int i = displayed; i >= 0; i--) {
                int displayOffset = i;
                int index = i;

                if (this.suggestions.size() - 1 > displayed) {
                    if (this.selectedSuggestion >= displayed / 2) {
                        if (this.selectedSuggestion <= this.suggestions.size() - 1 - displayed / 2) {
                            index = this.selectedSuggestion + i - displayed / 2;
                        } else {
                            index = this.suggestions.size() - 1 - i;
                            displayOffset = displayed - i;
                        }
                    }
                }

                String sugg = this.suggestions.get(index);
                font.drawString(this.suggestionX, this.height - 30 - displayOffset * charHeight, sugg, 0.3F, index == this.selectedSuggestion ? Colors.RED : Colors.WHITE);
            }
        }

        super.render(game, manager, g);
    }

    @Override
    public boolean onKeyPressed(IGameInstance game, int button) {
        List<String> lastInputs = game.getChatLog().getLastInputs();

        if (button == GLFW.GLFW_KEY_ENTER) {
            String text = this.inputField.getText();

            if (text != null && !text.isEmpty()) {
                if (RockBottomAPI.getNet().isClient()) {
                    RockBottomAPI.getNet().sendToServer(new SendChatPacket(text));
                } else {
                    game.getChatLog().sendCommandSenderMessage(text, game.getPlayer());
                }
                game.getChatLog().getLastInputs().add(0, text);

                this.inputField.setText("");

                if (game.getGuiManager().getGui() == this) {
                    game.getGuiManager().closeGui();
                }
                return true;
            } else {
                game.getGuiManager().closeGui();
            }

        } else if (button == GLFW.GLFW_KEY_DOWN) {
            if (this.suggestions.size() <= 1) {
                if (!lastInputs.isEmpty()) {
                    int newSelected = this.selectedLastInput - 1;
                    if (newSelected >= 0) {
                        this.inputField.setText(lastInputs.get(newSelected));
                        this.selectedLastInput = newSelected;
                        return true;
                    }
                }
            } else {
                this.selectedSuggestion--;
                if (this.selectedSuggestion < 0) {
                    this.selectedSuggestion = this.suggestions.size() - 1;
                }
                return true;
            }
        } else if (button == GLFW.GLFW_KEY_UP) {
            if (this.suggestions.size() <= 1) {
                if (!lastInputs.isEmpty()) {
                    int newSelected = this.selectedLastInput + 1;
                    if (newSelected < lastInputs.size()) {
                        this.inputField.setText(lastInputs.get(newSelected));
                        this.selectedLastInput = newSelected;
                        return true;
                    }
                }
            } else {
                this.selectedSuggestion++;
                if (this.selectedSuggestion >= this.suggestions.size()) {
                    this.selectedSuggestion = 0;
                }
                return true;
            }
        } else if (button == GLFW.GLFW_KEY_TAB) {
            if (!this.suggestions.isEmpty()) {
                String suggestion = this.suggestions.get(this.selectedSuggestion);
                String text = this.inputField.getText();

                int lastIndex = text.lastIndexOf(" ");
                if (lastIndex >= 0) {
                    this.inputField.setText(text.substring(0, lastIndex + 1) + suggestion);
                } else {
                    this.inputField.setText('/' + suggestion);
                }

                return true;
            }
        }
        return super.onKeyPressed(game, button);
    }

    @Override
    public boolean hasGradient() {
        return false;
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("chat");
    }
}
