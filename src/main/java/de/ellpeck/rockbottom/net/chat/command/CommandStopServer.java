package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class CommandStopServer extends Command{

    public CommandStopServer(){
        super(ResourceName.intern("stop_server"), "Stops the server", 10, "stop", "stop_server");
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat){
        if(game.isDedicatedServer()){
            game.getWorld().save();
            game.exit();
            return null;
        }
        else{
            return new ChatComponentText(FormattingCode.RED+"Can't stop as this is not a dedicated server!");
        }
    }
}
