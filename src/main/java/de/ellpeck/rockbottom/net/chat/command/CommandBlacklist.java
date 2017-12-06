package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.net.INetHandler;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;

import java.util.UUID;

public class CommandBlacklist extends Command{

    public CommandBlacklist(){
        super(RockBottomAPI.createInternalRes("blacklist"), "Modifies blacklisted players. Params: <'add'/'remove'> <uuid>", 8);
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat){
        if(args.length > 0){
            INetHandler net = RockBottomAPI.getNet();

            if("add".equals(args[0])){
                if(args.length > 1){
                    try{
                        UUID id = UUID.fromString(args[1]);
                        net.blacklist(id);
                        net.saveServerSettings();
                        return new ChatComponentText(FormattingCode.GREEN+"Added player "+id+" to the blacklist!");
                    }
                    catch(Exception e){
                        return new ChatComponentText(FormattingCode.RED+"Couldn't parse player id!");
                    }
                }
                else{
                    return new ChatComponentText(FormattingCode.RED+"Specify the player to add!");
                }
            }
            else if("remove".equals(args[0])){
                if(args.length > 1){
                    try{
                        UUID id = UUID.fromString(args[1]);
                        net.removeBlacklist(id);
                        net.saveServerSettings();
                        return new ChatComponentText(FormattingCode.GREEN+"Removed player "+id+" from the blacklist!");
                    }
                    catch(Exception e){
                        return new ChatComponentText(FormattingCode.RED+"Couldn't parse player id!");
                    }
                }
                else{
                    return new ChatComponentText(FormattingCode.RED+"Specify the player to remove!");
                }
            }
        }
        return new ChatComponentText(FormattingCode.RED+"Specify your action!");
    }
}
