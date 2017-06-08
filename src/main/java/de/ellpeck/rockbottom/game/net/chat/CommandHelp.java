package de.ellpeck.rockbottom.game.net.chat;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.assets.font.FormattingCode;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;

public class CommandHelp extends Command{

    public CommandHelp(){
        super("help", "/help for a list of all commands", 0);
    }

    @Override
    public String execute(String[] args, EntityPlayer player, String playerName, RockBottom game, AssetManager manager, ChatLog chat){
        chat.sendMessageToPlayer(player, FormattingCode.GREEN+"List of all commands:");

        for(Command command : ChatLog.COMMAND_REGISTRY.values()){
            chat.sendMessageToPlayer(player, FormattingCode.ORANGE+command.getName()+FormattingCode.WHITE+": "+FormattingCode.LIGHT_GRAY+command.getDescription());
        }

        chat.sendMessageToPlayer(player, FormattingCode.GRAY+"(Parameters in <angle brackets> are required, ones in [square brackets] are optional)");

        return null;
    }
}
