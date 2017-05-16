package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.font.FormattingCode;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.toclient.PacketChatMessage;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

import java.util.*;

public class ChatLog{

    public static final Map<String, Command> COMMAND_REGISTRY = new HashMap<>();

    static{
        registerCommand(new CommandHelp());
        registerCommand(new CommandAddPermission());
        registerCommand(new CommandSpawnItem());
        registerCommand(new CommandTeleport());
        registerCommand(new CommandMe());
    }

    public final List<String> messages = new ArrayList<>();
    private final List<Integer> newMessageCounter = new ArrayList<>();

    public static void registerCommand(Command command){
        COMMAND_REGISTRY.put(command.getName(), command);
    }

    public void displayMessage(String message){
        this.messages.add(0, message);
        this.newMessageCounter.add(0, 400);

        Log.info("Chat: "+message);
    }

    public void sendPlayerMessage(String message, EntityPlayer player, String playerName){
        if(NetHandler.isServer()){
            if(message.startsWith("/")){
                String cmdFeedback;

                String[] split = message.substring(1).split(" ");
                Command command = COMMAND_REGISTRY.get(split[0]);

                if(command != null){
                    if(player.getCommandLevel() >= command.getLevel()){
                        RockBottom game = RockBottom.get();
                        cmdFeedback = command.execute(Arrays.copyOfRange(split, 1, split.length), player, playerName, game, game.assetManager, this);
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

    public void sendMessageToPlayer(EntityPlayer player, String message){
        if(NetHandler.isServer()){
            if(NetHandler.isThePlayer(player)){
                this.displayMessage(message);
            }
            else{
                player.sendPacket(new PacketChatMessage(message));
            }
        }
    }

    public void broadcastMessage(String message){
        if(NetHandler.isServer()){
            this.displayMessage(message);
            NetHandler.sendToAllPlayers(RockBottom.get().world, new PacketChatMessage(message));
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
