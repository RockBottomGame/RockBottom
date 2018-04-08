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
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandBlacklist extends Command{

    public CommandBlacklist(){
        super(ResourceName.intern("blacklist"), "Modifies blacklisted players. Params: <'add'/'remove'> <uuid> [reason]", 8);
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat){
        if(args.length > 0){
            INetHandler net = RockBottomAPI.getNet();

            if("add".equals(args[0])){
                if(args.length > 1){
                    try{
                        UUID id = chat.getPlayerIdFromString(args[1]);

                        StringBuilder reason = new StringBuilder();
                        if(args.length > 2){
                            for(int i = 2; i < args.length; i++){
                                reason.append(args[i]).append(' ');
                            }
                        }

                        net.blacklist(id, reason.toString());
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
                        UUID id = chat.getPlayerIdFromString(args[1]);
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

    @Override
    public List<String> getAutocompleteSuggestions(String[] args, int argNumber, ICommandSender sender, IGameInstance game, IChatLog chat){
        if(argNumber == 0){
            return Arrays.asList("add", "remove");
        }
        else if(argNumber == 1){
            return chat.getPlayerSuggestions();
        }
        else{
            return Collections.emptyList();
        }
    }
}
