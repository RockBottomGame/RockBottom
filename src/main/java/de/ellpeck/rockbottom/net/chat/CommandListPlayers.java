package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;

import java.util.List;

public class CommandListPlayers extends Command{

    public CommandListPlayers(){
        super("players", "/players for a list of currently connected players", 0);
    }

    @Override
    public String execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat){
        List<AbstractEntityPlayer> players = game.getWorld().getAllPlayers();

        chat.sendMessageTo(sender, FormattingCode.GREEN.toString()+players.size()+" connected Players:");

        for(AbstractEntityPlayer player : players){
            chat.sendMessageTo(sender, FormattingCode.ORANGE+player.getName()+FormattingCode.GRAY+" ("+player.getUniqueId()+")");
        }

        return null;
    }
}
