package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.entity.player.GameMode;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author canitzp
 */
public class GameModeCommand extends Command {

    public GameModeCommand() {
        super(ResourceName.intern("gamemode"), "Set a players gamemode. Params: <'survival'/'creative'> [player]", 3, "gamemode", "gm");
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
    
        GameMode gameMode = null;
        AbstractPlayerEntity changed = null;
        
        if(args.length >= 1){
            String mode = args[0];
            switch (mode) {
                case "0": case "s": case "survival": {
                    gameMode = GameMode.SURVIVAL;
                    break;
                }
                case "1": case "c": case "creative": {
                    gameMode = GameMode.CREATIVE;
                    break;
                }
                default: {
                    return new TextChatComponent(FormattingCode.RED + "Gamemode '" + mode + "' does not exist!");
                }
            }
        }
        
        if(args.length == 2){
            AbstractPlayerEntity player = game.getWorld().getPlayer(args[1]);
            if (player != null) {
                changed = player;
            } else {
                return new TextChatComponent(FormattingCode.RED + "Can't find player '" + args[1] + "'!");
            }
        } else if(sender instanceof AbstractPlayerEntity){
            changed = (AbstractPlayerEntity) sender;
        }
        
        if(gameMode != null && changed != null){
            changed.setGameMode(gameMode);
            return new TextChatComponent(FormattingCode.GREEN + "Gamemode from player '" + changed.getName() + "' was changed to '" + gameMode.name() + "'.");
        }
        
        return new TextChatComponent(FormattingCode.RED + "An error occured in CommandGameMode!");
    }
    
    @Override
    public List<String> getAutocompleteSuggestions(String[] args, int argNumber, ICommandSender sender, IGameInstance game, IChatLog chat){
        switch(argNumber){
            case 0: {
                return Arrays.asList("survival", "creative");
            }
            case 1: {
                return chat.getPlayerSuggestions();
            }
        }
        return Collections.emptyList();
    }
}
