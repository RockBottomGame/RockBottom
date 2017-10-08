package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.ChatMessageEvent;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.net.chat.command.CommandHelp;
import de.ellpeck.rockbottom.net.chat.command.CommandSpawnItem;
import de.ellpeck.rockbottom.net.chat.command.CommandStopServer;
import de.ellpeck.rockbottom.net.chat.command.CommandTeleport;
import de.ellpeck.rockbottom.net.packet.toclient.PacketChatMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ChatLog implements IChatLog{

    private static final Logger CHAT_LOGGER = Logging.createLogger("Chat");

    static{
        new CommandHelp().register();
        new CommandStopServer().register();
        new CommandSpawnItem().register();
        new CommandTeleport().register();
    }

    private final List<ChatComponent> messages = new ArrayList<>();
    private final List<Integer> newMessageCounter = new ArrayList<>();

    @Override
    public void displayMessage(ChatComponent message){
        this.messages.add(0, message);

        if(!RockBottomAPI.getGame().isDedicatedServer()){
            this.newMessageCounter.add(0, 400);
        }

        CHAT_LOGGER.info(message.getUnformattedWithChildren());
    }

    @Override
    public void sendCommandSenderMessage(String message, ICommandSender sender){
        if(RockBottomAPI.getNet().isServer()){
            ChatMessageEvent event = new ChatMessageEvent(this, sender, message);
            if(RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED){
                message = event.message;

                if(message.startsWith("/")){
                    ChatComponent cmdFeedback;

                    String[] split = message.substring(1).split(" ");
                    Command command = this.getCommand(split[0]);

                    if(command != null){
                        if(sender.getCommandLevel() >= command.getLevel()){
                            IGameInstance game = RockBottomAPI.getGame();
                            cmdFeedback = command.execute(Arrays.copyOfRange(split, 1, split.length), sender, sender.getName(), game, this);
                        }
                        else{
                            cmdFeedback = new ChatComponentText(FormattingCode.RED+"You are not allowed to execute this command!");
                        }
                    }
                    else{
                        cmdFeedback = new ChatComponentText(FormattingCode.RED+"Unknown command, use /help for a list of commands.");
                    }

                    if(cmdFeedback != null){
                        this.sendMessageTo(sender, cmdFeedback);
                    }

                    CHAT_LOGGER.info("Command sender "+sender.getName()+" with id "+sender.getUniqueId()+" executed command '/"+split[0]+"' with feedback '"+cmdFeedback+"'");
                }
                else{
                    this.broadcastMessage(new ChatComponentText(sender.getChatColorFormat()+"["+sender.getName()+"] &4"+message));
                }
            }
        }
    }

    private Command getCommand(String name){
        if(name.contains(Constants.RESOURCE_SEPARATOR)){
            return RockBottomAPI.COMMAND_REGISTRY.get(RockBottomAPI.createRes(name));
        }
        else{
            for(Command command : RockBottomAPI.COMMAND_REGISTRY.getUnmodifiable().values()){
                for(String s : command.getTriggers()){
                    if(name.equals(s)){
                        return command;
                    }
                }
            }
            return null;
        }
    }

    @Override
    public void sendMessageTo(ICommandSender sender, ChatComponent message){
        sender.sendMessageTo(this, message);
    }

    @Override
    public void broadcastMessage(ChatComponent message){
        this.displayMessage(message);

        if(RockBottomAPI.getNet().isServer()){
            RockBottomAPI.getNet().sendToAllPlayers(RockBottomAPI.getGame().getWorld(), new PacketChatMessage(message));
        }
    }

    @Override
    public List<ChatComponent> getMessages(){
        return this.messages;
    }

    @Override
    public void clear(){
        this.messages.clear();
        this.newMessageCounter.clear();
    }

    public void drawNewMessages(RockBottom game, IAssetManager manager, IGraphics g){
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
