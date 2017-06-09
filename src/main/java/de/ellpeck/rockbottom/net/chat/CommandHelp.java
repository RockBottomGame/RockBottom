package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;

public class CommandHelp extends Command{

    public CommandHelp(){
        super("help", "/help for a list of all commands", 0);
    }

    @Override
    public String execute(String[] args, AbstractEntityPlayer player, String playerName, IGameInstance game, IAssetManager manager, IChatLog chat){
        chat.sendMessageToPlayer(player, FormattingCode.GREEN+"List of all commands:");

        for(Command command : RockBottomAPI.COMMAND_REGISTRY.values()){
            chat.sendMessageToPlayer(player, FormattingCode.ORANGE+command.getName()+FormattingCode.WHITE+": "+FormattingCode.LIGHT_GRAY+command.getDescription());
        }

        chat.sendMessageToPlayer(player, FormattingCode.GRAY+"(Parameters in <angle brackets> are required, ones in [square brackets] are optional)");

        return null;
    }
}
