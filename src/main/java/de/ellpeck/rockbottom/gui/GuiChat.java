package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentFormatSelector;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.net.packet.toserver.PacketSendChat;
import org.newdawn.slick.Input;

import java.util.List;

public class GuiChat extends Gui{

    private static final int BACKING_ONE = Colors.rgb(0F, 0F, 0F, 0.65F);
    private static final int BACKING_TWO = Colors.rgb(0.1F, 0.1F, 0.1F, 0.65F);
    public static boolean isSelectorOpen;
    private ComponentFormatSelector selector;
    private ComponentInputField inputField;

    @Override
    public void onClosed(IGameInstance game){
        super.onClosed(game);

        isSelectorOpen = this.selector.isMenuOpen();
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        this.inputField = new ComponentInputField(this, 5, this.height-21, this.width/2-18, 16, true, false, true, 512, true);
        this.components.add(this.inputField);

        this.selector = new ComponentFormatSelector(this, 5+this.width/2-16, this.height-21, this.inputField);
        this.components.add(this.selector);
        if(isSelectorOpen){
            this.selector.openMenu();
        }
    }

    @Override
    public boolean onKeyboardAction(IGameInstance game, int button, char character){
        if(button == Input.KEY_ENTER){
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
        return super.onKeyboardAction(game, button, character);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g){
        drawMessages(game, manager, g, game.getChatLog().getMessages(), 20);
        super.render(game, manager, g);
    }

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
    public boolean hasGradient(){
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("chat");
    }
}
