package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.toclient.PacketChatMessage;
import de.ellpeck.rockbottom.net.packet.toserver.PacketSendChat;
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
            NetHandler.sendToAllPlayers(RockBottom.get().world, new PacketChatMessage(message));
        }
        else if(NetHandler.isClient()){
            NetHandler.sendToServer(new PacketSendChat(RockBottom.get().player.getUniqueId(), message));
        }
    }

    public void drawNewMessages(RockBottom game, AssetManager manager, Graphics g){
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
