package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentFormatSelector;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.net.packet.toserver.PacketSendChat;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class GuiChat extends Gui{

    public static boolean isSelectorOpen;

    private static final int BACKING_ONE = Colors.rgb(0F, 0F, 0F, 0.65F);
    private static final int BACKING_TWO = Colors.rgb(0.1F, 0.1F, 0.1F, 0.65F);

    private ComponentFormatSelector selector;
    private ComponentInputField inputField;

    private final List<String> suggestions = new ArrayList<>();
    private int selectedSuggestion;
    private float suggestionX;

    public static void drawMessages(IGameInstance game, IAssetManager manager, IGraphics g, List<ChatComponent> messages, int maxCount){
        IFont font = manager.getFont();
        float scale = 0.25F;
        float fontHeight = font.getHeight(scale);
        int sizeX = (int)g.getWidthInGui()/2;
        int y = (int)g.getHeightInGui()-26-(int)fontHeight;

        boolean alternate = game.getChatLog().getMessages().size()%2 == 0;
        int messageCounter = 0;

        for(ChatComponent message : messages){
            List<String> split = font.splitTextToLength(sizeX, scale, true, message.getDisplayWithChildren(game, manager));

            g.fillRect(5, y-fontHeight*(split.size()-1), sizeX, fontHeight*split.size()+1, alternate ? BACKING_ONE : BACKING_TWO);

            for(int i = split.size()-1; i >= 0; i--){
                String s = split.get(i);

                font.drawString(6, y+1, s, scale);

                y -= fontHeight;
            }

            y -= 1;
            alternate = !alternate;

            messageCounter++;
            if(messageCounter >= maxCount){
                break;
            }
        }
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        IChatLog chat = game.getChatLog();
        this.inputField = new ComponentInputField(this, 5, this.height-21, this.width/2-18, 16, true, false, true, 512, true, strg -> {
            this.suggestions.clear();
            this.suggestionX = 8+game.getAssetManager().getFont().getWidth(strg, 0.35F);
            this.selectedSuggestion = 0;

            if(strg.startsWith("/")){
                String[] split = strg.substring(1).split(" ");
                if(split.length > 0){
                    Command command = chat.getCommand(split[0]);
                    if(command != null){
                        int index = strg.endsWith(" ") ? split.length : split.length-1;

                        for(String suggestion : command.getAutocompleteSuggestions(index, game.getPlayer(), game, game.getChatLog())){
                            if(index == split.length || (suggestion.startsWith(split[index]) && !suggestion.equals(split[index]))){
                                this.suggestions.add(suggestion);
                            }
                        }

                        return;
                    }
                }

                for(Command command : RockBottomAPI.COMMAND_REGISTRY.getUnmodifiable().values()){
                    for(String trigger : command.getTriggers()){
                        if(split.length <= 0 || trigger.startsWith(split[0])){
                            this.suggestions.add(trigger);
                        }
                    }
                }
            }
        });
        this.components.add(this.inputField);

        this.selector = new ComponentFormatSelector(this, 5+this.width/2-16, this.height-21, this.inputField);
        this.components.add(this.selector);
        if(isSelectorOpen){
            this.selector.openMenu();
        }
    }

    @Override
    public void onClosed(IGameInstance game){
        super.onClosed(game);

        isSelectorOpen = this.selector.isMenuOpen();
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g){
        drawMessages(game, manager, g, game.getChatLog().getMessages(), 20);

        if(!this.suggestions.isEmpty()){
            IFont font = manager.getFont();
            float charHeight = font.getHeight(0.3F);

            float longestWidth = 0F;
            for(String sugg : this.suggestions){
                float width = font.getWidth(sugg, 0.3F);
                if(width > longestWidth){
                    longestWidth = width;
                }
            }

            float x = this.suggestionX-1;
            float y = this.height-30-(this.suggestions.size()-1)*charHeight-1;
            float width = longestWidth+1;
            float height = this.suggestions.size()*charHeight+1;

            g.fillRect(x, y, width, height, Gui.HOVER_INFO_BACKGROUND);
            g.drawRect(x, y, width, height, Gui.GRADIENT_COLOR);

            for(int i = this.suggestions.size()-1; i >= 0; i--){
                String sugg = this.suggestions.get(i);
                font.drawString(this.suggestionX, this.height-30-i*charHeight, sugg, 0.3F, i == this.selectedSuggestion ? Colors.RED : Colors.WHITE);
            }
        }

        super.render(game, manager, g);
    }

    @Override
    public boolean onKeyboardAction(IGameInstance game, int button, char character){
        if(button == Keyboard.KEY_RETURN){
            String text = this.inputField.getText();

            if(text != null && !text.isEmpty()){
                if(RockBottomAPI.getNet().isClient()){
                    RockBottomAPI.getNet().sendToServer(new PacketSendChat(game.getPlayer().getUniqueId(), text));
                }
                else{
                    game.getChatLog().sendCommandSenderMessage(text, game.getPlayer());
                }

                this.inputField.setText("");
                game.getGuiManager().closeGui();
                return true;
            }
            else{
                game.getGuiManager().closeGui();
            }

        }
        else if(button == Keyboard.KEY_DOWN){
            this.selectedSuggestion--;
            if(this.selectedSuggestion < 0){
                this.selectedSuggestion = this.suggestions.size()-1;
            }
            return true;
        }
        else if(button == Keyboard.KEY_UP){
            this.selectedSuggestion++;
            if(this.selectedSuggestion >= this.suggestions.size()){
                this.selectedSuggestion = 0;
            }
            return true;
        }
        else if(button == Keyboard.KEY_TAB){
            if(!this.suggestions.isEmpty()){
                String suggestion = this.suggestions.get(this.selectedSuggestion);
                String text = this.inputField.getText();

                int lastIndex = text.lastIndexOf(" ");
                if(lastIndex >= 0){
                    this.inputField.setText(text.substring(0, lastIndex+1)+suggestion);
                }
                else{
                    this.inputField.setText("/"+suggestion);
                }

                return true;
            }
        }
        return super.onKeyboardAction(game, button, character);
    }

    @Override
    public boolean hasGradient(){
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("chat");
    }
}
