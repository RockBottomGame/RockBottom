package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.INetHandler;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.List;

public class PermissionCommand extends Command {

    public PermissionCommand() {
        super(ResourceName.intern("permissions"), "Sets the command permission level of the given player. Params: /permissions <player> <'admin'/level>", Constants.ADMIN_PERMISSION, "perms");
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
        if (args.length < 2) {
            return new TextChatComponent(FormattingCode.RED + "Must provide player name and level.");
        }
        INetHandler net = RockBottomAPI.getNet();
        AbstractPlayerEntity player = game.getWorld().getPlayer(args[0]);
        if (player != null) {
            int oldLevel = player.getCommandLevel();
            int level;
            if ("admin".equals(args[1])) {
                level = Constants.ADMIN_PERMISSION;
            } else {
                try {
                    level = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    return new TextChatComponent(FormattingCode.RED + "Permission level must be a whole number; was '" + args[1] + "'");
                }
            }
            net.setCommandLevel(player.getUniqueId(), level);
            net.saveServerSettings();
            return new TextChatComponent(FormattingCode.GREEN + "Changed command permission level for player '" + args[0] + "' from '" + oldLevel + "' to '" + level + "'");
        }

        return new TextChatComponent(FormattingCode.RED + "Could not find player '" + args[0] + "'");
    }

    @Override
    public List<String> getAutocompleteSuggestions(String[] args, int argNumber, ICommandSender sender, IGameInstance game, IChatLog chat) {
        return super.getAutocompleteSuggestions(args, argNumber, sender, game, chat);
    }
}
