package de.ellpeck.rockbottom.game.net.chat;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.data.settings.CommandPermissions;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;

import java.util.Arrays;
import java.util.UUID;

public class CommandAddPermission extends Command{

    public CommandAddPermission(){
        super("addperm", "/addperm <player unique id> <level>", Constants.ADMIN_PERMISSION);
    }

    @Override
    public String execute(String[] args, AbstractEntityPlayer player, String playerName, IGameInstance game, IAssetManager manager, IChatLog chat){
        try{
            UUID id = UUID.fromString(args[0]);
            int level = Integer.parseInt(args[1]);

            CommandPermissions perms = RockBottomAPI.getNet().getCommandPermissions();
            perms.setCommandLevel(id, level);

            game.getDataManager().savePropSettings(perms);

            return FormattingCode.GREEN+"Added permission level of "+level+" for player with id "+id+"!";
        }
        catch(Exception e){
            return FormattingCode.RED+"Error formatting unique id or number for command args "+Arrays.toString(args)+"!";
        }
    }
}
