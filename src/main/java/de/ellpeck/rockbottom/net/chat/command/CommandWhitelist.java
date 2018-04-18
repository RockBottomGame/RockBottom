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

public class CommandWhitelist extends Command{

    public CommandWhitelist(){
        super(ResourceName.intern("whitelist"), "Modifies whitelisted players or enables/disables it. Params: <'add'/'remove'/'enable'/'disable'> [uuid]", 7);
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat){
        if(args.length > 0){
            INetHandler net = RockBottomAPI.getNet();

            if("add".equals(args[0])){
                if(args.length > 1){
                    UUID id = chat.getPlayerIdFromString(args[1]);
                    if(id != null){
                        net.whitelist(id);
                        net.saveServerSettings();
                        return new ChatComponentText(FormattingCode.GREEN+"Added player "+id+" to the whitelist!");
                    }
                    else{
                        return new ChatComponentText(FormattingCode.RED+"Couldn't parse player id!");
                    }
                }
                else{
                    return new ChatComponentText(FormattingCode.RED+"Specify the player to add!");
                }
            }
            else if("remove".equals(args[0])){
                if(args.length > 1){
                    UUID id = chat.getPlayerIdFromString(args[1]);
                    if(id != null){
                        net.removeWhitelist(id);
                        net.saveServerSettings();
                        return new ChatComponentText(FormattingCode.GREEN+"Removed player "+id+" from the whitelist!");
                    }
                    else{
                        return new ChatComponentText(FormattingCode.RED+"Couldn't parse player id!");
                    }
                }
                else{
                    return new ChatComponentText(FormattingCode.RED+"Specify the player to remove!");
                }
            }
            else if("enable".equals(args[0])){
                net.enableWhitelist(true);
                net.saveServerSettings();
                return new ChatComponentText(FormattingCode.GREEN+"Enabled whitelist!");
            }
            else if("disable".equals(args[0])){
                net.enableWhitelist(false);
                net.saveServerSettings();
                return new ChatComponentText(FormattingCode.GREEN+"Disabled whitelist!");
            }
        }
        return new ChatComponentText(FormattingCode.RED+"Specify your action!");
    }

    @Override
    public List<String> getAutocompleteSuggestions(String[] args, int argNumber, ICommandSender sender, IGameInstance game, IChatLog chat){
        if(argNumber == 0){
            return Arrays.asList("add", "remove", "enable", "disable");
        }
        else if(argNumber == 1 && ("add".equals(args[0]) || "remove".equals(args[0]))){
            return chat.getPlayerSuggestions();
        }
        else{
            return Collections.emptyList();
        }
    }
}
