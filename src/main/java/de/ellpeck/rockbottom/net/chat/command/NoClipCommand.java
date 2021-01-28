package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Collections;
import java.util.List;

public class NoClipCommand extends Command {

    public NoClipCommand() {
        super(ResourceName.intern("noclip"), "Toggles noclip. Params: [player]", 3, "noclip");
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {

        AbstractPlayerEntity player = null;
        if(args.length == 1) {
            player = game.getWorld().getPlayer(args[0]);
            if (player == null) {
                return new TextChatComponent(FormattingCode.RED + "Can't find player '" + args[0] + "'!");
            }
        } else if(sender instanceof AbstractPlayerEntity){
            player = (AbstractPlayerEntity) sender;
        }

        if(player != null){
            player.setNoClip(!player.isNoClip());
            return new TextChatComponent(FormattingCode.GREEN + "Player '" + player.getName() + "' no clip was set to '" + player.isNoClip() + "'.");
        }

        return new TextChatComponent(FormattingCode.RED + "Could not find player. Is it being called from the console?");
    }

    @Override
    public List<String> getAutocompleteSuggestions(String[] args, int argNumber, ICommandSender sender, IGameInstance game, IChatLog chat){
        if (argNumber == 0) {
            return chat.getPlayerSuggestions();
        }
        return Collections.emptyList();
    }
}
