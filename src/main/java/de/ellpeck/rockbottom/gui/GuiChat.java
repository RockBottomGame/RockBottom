package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.net.packet.toserver.PacketSendChat;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.util.List;

public class GuiChat extends Gui{

    private static final Color BACKING_ONE = new Color(0F, 0F, 0F, 0.65F);
    private static final Color BACKING_TWO = new Color(0.1F, 0.1F, 0.1F, 0.65F);

    private ComponentInputField inputField;

    public GuiChat(){
        super(100, 100);
    }

    public static void drawMessages(IGameInstance game, IAssetManager manager, Graphics g, List<ChatComponent> messages, int maxCount){
        Font font = manager.getFont();
        float scale = 0.25F;
        float fontHeight = font.getHeight(scale);
        int sizeX = (int)game.getWidthInGui()/2;
        int y = (int)game.getHeightInGui()-26-(int)fontHeight;

        boolean alternate = game.getChatLog().getMessages().size()%2 == 0;
        int messageCounter = 0;

        for(ChatComponent message : messages){
            List<String> split = font.splitTextToLength(sizeX, scale, true, message.getDisplayWithChildren(game, manager));

            g.setColor(alternate ? BACKING_ONE : BACKING_TWO);
            g.fillRect(5, y-fontHeight*(split.size()-1), sizeX, fontHeight*split.size()+1);

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
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.inputField = new ComponentInputField(this, 5, (int)game.getHeightInGui()-21, (int)game.getWidthInGui()/2, 16, true, false, true, 512, true);
        this.components.add(this.inputField);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        drawMessages(game, manager, g, game.getChatLog().getMessages(), 20);
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
    public boolean hasGradient(){
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("chat");
    }
}
