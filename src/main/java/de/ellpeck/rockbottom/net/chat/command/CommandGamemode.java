package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.Gamemode;

/**
 * @author canitzp
 */
public class CommandGamemode extends Command {

    public CommandGamemode() {
        super(ResourceName.intern("gamemode"), "WIP", 3, "gamemode", "gm");
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
        if(sender instanceof EntityPlayer){
            if(args.length == 1){
                String mode = args[0];
                switch (mode) {
                    case "0": case "s": case "survival": {
                        ((EntityPlayer) sender).setGamemode(Gamemode.SURVIVAL);
                        break;
                    }
                    case "1": case "c": case "creative": {
                        ((EntityPlayer) sender).setGamemode(Gamemode.CREATIVE);
                        break;
                    }
                    default: {
                        return null;
                    }
                }
                return new ChatComponentText("Gamemode was set to " + mode);
            }
        }
        return null;
    }

    @Override
    public int getMaxArgumentAmount() {
        return 1;
    }
}
