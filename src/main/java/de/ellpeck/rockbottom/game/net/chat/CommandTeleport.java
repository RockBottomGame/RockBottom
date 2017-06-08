package de.ellpeck.rockbottom.game.net.chat;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.assets.font.FormattingCode;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;

import java.util.Arrays;

public class CommandTeleport extends Command{

    public CommandTeleport(){
        super("teleport", "/teleport <x> <y>", 5);
    }

    @Override
    public String execute(String[] args, EntityPlayer player, String playerName, IGameInstance game, AssetManager manager, ChatLog chat){
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
