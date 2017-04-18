package de.ellpeck.game.gui;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.assets.font.Font;
import de.ellpeck.game.gui.component.ComponentInputField;
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

    @Override
    public void initGui(Game game){
        super.initGui(game);

        this.inputField = new ComponentInputField(this, 5, (int)game.getHeightInGui()-21, (int)game.getWidthInGui()/2, 16, true, false, false, true);
        this.components.add(this.inputField);
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        drawMessages(game, manager, g, game.chatLog.messages, 20);
    }

    public static void drawMessages(Game game, AssetManager manager, Graphics g, List<String> messages, int maxCount){
        Font font = manager.getFont();
        float scale = 0.25F;
        float fontHeight = font.getHeight(scale);
        int sizeX = (int)game.getWidthInGui()/2;
        int y = (int)game.getHeightInGui()-26-(int)fontHeight;

        boolean alternate = game.chatLog.messages.size()%2 == 0;
        int messageCounter = 0;

        for(String message : messages){
            List<String> split = font.splitTextToLength(sizeX, scale, message);

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
    public boolean onKeyboardAction(Game game, int button, char character){
        if(button == Input.KEY_ENTER){
            String text = this.inputField.getText();

            if(text != null && !text.isEmpty()){
                Color color = game.player.color;
                game.chatLog.sendMessage("&("+color.r+","+color.g+","+color.b+")["+game.settings.chatName+"] &4"+text);
                this.inputField.setText("");

                return true;
            }
        }
        return super.onKeyboardAction(game, button, character);
    }

    @Override
    public boolean hasGradient(){
        return false;
    }
}
