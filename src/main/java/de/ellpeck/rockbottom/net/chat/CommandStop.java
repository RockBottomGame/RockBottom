package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;

public class CommandStop extends Command{

    public CommandStop(){
        super("stopserver", "Saves and stops the server", Constants.ADMIN_PERMISSION);
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat){
        game.scheduleAction(() -> {
            game.getWorld().save();
            game.exit();
            return true;
        });
        return new ChatComponentText(FormattingCode.GREEN+"Saving and stopping the server...");
    }
}
