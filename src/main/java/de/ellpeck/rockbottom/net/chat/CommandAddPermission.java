package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.Constants;
import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.font.FormattingCode;
import de.ellpeck.rockbottom.data.settings.CommandPermissions;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.Arrays;
import java.util.UUID;

public class CommandAddPermission extends Command{

    public CommandAddPermission(){
        super("addperm", "/addperm <player unique id> <level>", Constants.ADMIN_PERMISSION);
    }

    @Override
    public String execute(String[] args, EntityPlayer player, String playerName, RockBottom game, AssetManager manager, ChatLog chat){
        try{
            UUID id = UUID.fromString(args[0]);
            int level = Integer.parseInt(args[1]);

            CommandPermissions perms = NetHandler.getCommandPermissions();
            perms.setCommandLevel(id, level);

            game.dataManager.savePropSettings(perms);

            return FormattingCode.GREEN+"Added permission level of "+level+" for player with id "+id+"!";
        }
        catch(Exception e){
            return FormattingCode.RED+"Error formatting unique id or number for command args "+Arrays.toString(args)+"!";
        }
    }
}
