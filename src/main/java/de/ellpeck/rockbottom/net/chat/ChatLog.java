package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.net.packet.toclient.PacketChatMessage;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatLog implements IChatLog{

    static{
        IChatLog.registerCommand(new CommandHelp());
        IChatLog.registerCommand(new CommandAddPermission());
        IChatLog.registerCommand(new CommandSpawnItem());
        IChatLog.registerCommand(new CommandTeleport());
        IChatLog.registerCommand(new CommandMe());
    }

    private final List<String> messages = new ArrayList<>();
    private final List<Integer> newMessageCounter = new ArrayList<>();

    @Override
    public void displayMessage(String message){
        this.messages.add(0, message);
        this.newMessageCounter.add(0, 400);

        Log.info("Chat: "+message);
    }

    @Override
    public void sendPlayerMessage(String message, AbstractEntityPlayer player, String playerName){
        if(RockBottomAPI.getNet().isServer()){
            if(message.startsWith("/")){
                String cmdFeedback;

                String[] split = message.substring(1).split(" ");
                Command command = RockBottomAPI.COMMAND_REGISTRY.get(split[0]);

                if(command != null){
                    if(player.getCommandLevel() >= command.getLevel()){
                        IGameInstance game = RockBottom.get();
                        cmdFeedback = command.execute(Arrays.copyOfRange(split, 1, split.length), player, playerName, game, game.getAssetManager(), this);
                    }
                    else{
                        cmdFeedback = FormattingCode.RED+"You are not allowed to execute this command!";
                    }
                }
                else{
                    cmdFeedback = FormattingCode.RED+"Unknown command, use /help for a list of commands.";
                }

                Log.info("Player with id "+player.getUniqueId()+" executed command '/"+split[0]+"' with feedback "+cmdFeedback);

                if(cmdFeedback != null){
                    this.sendMessageToPlayer(player, cmdFeedback);
                }
            }
            else{
                this.broadcastMessage(player.getChatColorFormat()+"["+playerName+"] &4"+message);
            }
        }
    }

    @Override
    public void sendMessageToPlayer(AbstractEntityPlayer player, String message){
        if(RockBottomAPI.getNet().isServer()){
            if(RockBottomAPI.getNet().isThePlayer(player)){
                this.displayMessage(message);
            }
            else{
                player.sendPacket(new PacketChatMessage(message));
            }
        }
    }

    @Override
    public void broadcastMessage(String message){
        if(RockBottomAPI.getNet().isServer()){
            this.displayMessage(message);
            RockBottomAPI.getNet().sendToAllPlayers(RockBottom.get().getWorld(), new PacketChatMessage(message));
        }
    }

    @Override
    public List<String> getMessages(){
        return this.messages;
    }

    public void drawNewMessages(RockBottom game, IAssetManager manager, Graphics g){
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
