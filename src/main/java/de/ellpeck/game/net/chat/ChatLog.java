package de.ellpeck.game.net.chat;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.GuiChat;
import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.packet.toclient.PacketChatMessage;
import de.ellpeck.game.net.packet.toserver.PacketSendChat;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class ChatLog{

    public final List<String> messages = new ArrayList<>();
    private final List<Integer> newMessageCounter = new ArrayList<>();

    public void addMessage(String message){
        this.messages.add(0, message);
        this.newMessageCounter.add(0, 400);
    }

    public void sendMessage(String message){
        this.addMessage(message);

        if(NetHandler.isServer()){
            NetHandler.sendToAllPlayers(Game.get().world, new PacketChatMessage(message));
        }
        else if(NetHandler.isClient()){
            NetHandler.sendToServer(new PacketSendChat(Game.get().player.getUniqueId(), message));
        }
    }

    public void drawNewMessages(Game game, AssetManager manager, Graphics g){
        if(!this.newMessageCounter.isEmpty()){
            GuiChat.drawMessages(game, manager, g, this.messages, this.newMessageCounter.size());
        }
    }

    public void updateNewMessages(){
        if(!this.newMessageCounter.isEmpty()){
            for(int i = 0; i < this.newMessageCounter.size(); i++){
                int newAmount = this.newMessageCounter.get(i)-1;

                if(newAmount > 0){
                    this.newMessageCounter.set(i, newAmount);
                }
                else{
                    this.newMessageCounter.remove(i);
                    i--;
                }
            }
        }
    }
}
