package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.font.FormattingCode;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.Arrays;

public class CommandTeleport extends Command{

    public CommandTeleport(){
        super("teleport", 5);
    }

    @Override
    public String execute(String[] args, EntityPlayer player, RockBottom game, AssetManager manager){
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
