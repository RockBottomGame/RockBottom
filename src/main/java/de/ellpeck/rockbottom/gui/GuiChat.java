package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.font.Font;
import de.ellpeck.rockbottom.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.net.NetHandler;
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

    public static void drawMessages(RockBottom game, AssetManager manager, Graphics g, List<String> messages, int maxCount){
        Font font = manager.getFont();
        float scale = 0.25F;
        float fontHeight = font.getHeight(scale);
        int sizeX = (int)game.getWidthInGui()/2;
        int y = (int)game.getHeightInGui()-26-(int)fontHeight;

        boolean alternate = game.chatLog.messages.size()%2 == 0;
        int messageCounter = 0;

        for(String message : messages){
            List<String> split = font.splitTextToLength(sizeX, scale, true, message);

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
    public void initGui(RockBottom game){
        super.initGui(game);

        this.inputField = new ComponentInputField(this, 5, (int)game.getHeightInGui()-21, (int)game.getWidthInGui()/2, 16, true, false, true, 512, true);
        this.components.add(this.inputField);
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        drawMessages(game, manager, g, game.chatLog.messages, 20);
    }

    @Override
    public boolean onKeyboardAction(RockBottom game, int button, char character){
        if(button == Input.KEY_ENTER){
            String text = this.inputField.getText();

            if(text != null && !text.isEmpty()){
                Color color = game.player.color;
                String name = "&("+color.r+","+color.g+","+color.b+")["+game.settings.chatName+"]";

                if(NetHandler.isClient()){
                    NetHandler.sendToServer(new PacketSendChat(game.player.getUniqueId(), text, name));
                }
                else{
                    game.chatLog.sendPlayerMessage(text, game.player, name);
                }

                this.inputField.setText("");
                game.guiManager.closeGui();
                return true;
            }
            else{
                game.guiManager.closeGui();
            }

        }
        return super.onKeyboardAction(game, button, character);
    }

    @Override
    public boolean hasGradient(){
        return false;
    }
}
