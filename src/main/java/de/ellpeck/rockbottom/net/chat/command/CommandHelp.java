package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;

import java.util.Arrays;

public class CommandHelp extends Command{

    public CommandHelp(){
        super(RockBottomAPI.createInternalRes("help"), "Shows a list of possible commands", 0, "help", "?");
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat){
        String s = FormattingCode.GREEN+"All Commands: \n";

        for(Command command : RockBottomAPI.COMMAND_REGISTRY.getUnmodifiable().values()){
            s += FormattingCode.ORANGE+Arrays.toString(command.getTriggers())+FormattingCode.RESET_COLOR+": "+FormattingCode.LIGHT_GRAY+command.getDescription()+" "+FormattingCode.GRAY+"(Level "+command.getLevel()+"+)\n";
        }

        return new ChatComponentText(s);
    }
}
