package de.ellpeck.rockbottom.game.net.chat;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.assets.font.FormattingCode;
import de.ellpeck.rockbottom.game.data.settings.CommandPermissions;
import de.ellpeck.rockbottom.game.net.NetHandler;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;

import java.util.Arrays;
import java.util.UUID;

public class CommandAddPermission extends Command{

    public CommandAddPermission(){
        super("addperm", "/addperm <player unique id> <level>", Constants.ADMIN_PERMISSION);
    }

    @Override
    public String execute(String[] args, EntityPlayer player, String playerName, IGameInstance game, AssetManager manager, ChatLog chat){
        try{
            UUID id = UUID.fromString(args[0]);
            int level = Integer.parseInt(args[1]);

            CommandPermissions perms = NetHandler.getCommandPermissions();
            perms.setCommandLevel(id, level);

            game.getDataManager().savePropSettings(perms);

            return FormattingCode.GREEN+"Added permission level of "+level+" for player with id "+id+"!";
        }
        catch(Exception e){
            return FormattingCode.RED+"Error formatting unique id or number for command args "+Arrays.toString(args)+"!";
        }
    }
}
