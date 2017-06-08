package de.ellpeck.rockbottom.game.net.chat;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;

import java.util.Arrays;

public class CommandTeleport extends Command{

    public CommandTeleport(){
        super("teleport", "/teleport <x> <y>", 5);
    }

    @Override
    public String execute(String[] args, AbstractEntityPlayer player, String playerName, IGameInstance game, IAssetManager manager, IChatLog chat){
        try{
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            player.setPos(x, y);

            return FormattingCode.GREEN+"Teleported to "+x+", "+y+"!";
        }
        catch(Exception e){
            return FormattingCode.RED+"Error formatting number for command args "+Arrays.toString(args)+"!";
        }
    }
}
