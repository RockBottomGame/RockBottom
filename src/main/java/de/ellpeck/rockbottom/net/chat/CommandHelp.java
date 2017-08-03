package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;

public class CommandHelp extends Command{

    public CommandHelp(){
        super("help", "/help for a list of all commands", 0);
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat){
        chat.sendMessageTo(sender, new ChatComponentText(FormattingCode.GREEN+"List of all commands:"));

        for(Command command : RockBottomAPI.COMMAND_REGISTRY.values()){
            chat.sendMessageTo(sender, new ChatComponentText(FormattingCode.ORANGE+command.getName()+FormattingCode.WHITE+": "+FormattingCode.LIGHT_GRAY+command.getDescription()));
        }

        chat.sendMessageTo(sender, new ChatComponentText(FormattingCode.GRAY+"(Parameters in <angle brackets> are required, ones in [square brackets] are optional)"));

        return null;
    }
}
