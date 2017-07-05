package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.ChatMessageEvent;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.init.RockBottom;
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
        IChatLog.registerCommand(new CommandStop());
        IChatLog.registerCommand(new CommandListPlayers());
    }

    private final List<String> messages = new ArrayList<>();
    private final List<Integer> newMessageCounter = new ArrayList<>();

    @Override
    public void displayMessage(String message){
        this.messages.add(0, message);

        if(!RockBottomAPI.getGame().isDedicatedServer()){
            this.newMessageCounter.add(0, 400);
        }

        Log.info("Chat: "+message);
    }

    @Override
    public void sendCommandSenderMessage(String message, ICommandSender sender){
        if(RockBottomAPI.getNet().isServer()){
            ChatMessageEvent event = new ChatMessageEvent(this, sender, message);
            if(RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED){
                message = event.message;

                if(message.startsWith("/")){
                    String cmdFeedback;

                    String[] split = message.substring(1).split(" ");
                    Command command = RockBottomAPI.COMMAND_REGISTRY.get(split[0]);

                    if(command != null){
                        if(sender.getCommandLevel() >= command.getLevel()){
                            IGameInstance game = AbstractGame.get();
                            cmdFeedback = command.execute(Arrays.copyOfRange(split, 1, split.length), sender, sender.getName(), game, this);
                        }
                        else{
                            cmdFeedback = FormattingCode.RED+"You are not allowed to execute this command!";
                        }
                    }
                    else{
                        cmdFeedback = FormattingCode.RED+"Unknown command, use /help for a list of commands.";
                    }

                    if(cmdFeedback != null){
                        this.sendMessageTo(sender, cmdFeedback);
                    }

                    Log.info("Command sender "+sender.getName()+" with id "+sender.getUniqueId()+" executed command '/"+split[0]+"' with feedback "+cmdFeedback);
                }
                else{
                    this.broadcastMessage(sender.getChatColorFormat()+"["+sender.getName()+"] &4"+message);
                }
            }
        }
    }

    @Override
    public void sendMessageTo(ICommandSender sender, String message){
        if(RockBottomAPI.getNet().isServer()){
            sender.sendMessageTo(this, message);
        }
    }

    @Override
    public void broadcastMessage(String message){
        if(RockBottomAPI.getNet().isServer()){
            this.displayMessage(message);
            RockBottomAPI.getNet().sendToAllPlayers(AbstractGame.get().getWorld(), new PacketChatMessage(message));
        }
    }

    @Override
    public List<String> getMessages(){
        return this.messages;
    }

    @Override
    public void clear(){
        this.messages.clear();
        this.newMessageCounter.clear();
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
