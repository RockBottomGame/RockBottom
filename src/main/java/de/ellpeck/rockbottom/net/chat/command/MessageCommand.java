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

public class MessageCommand extends Command {

    public MessageCommand() {
        super(ResourceName.intern("message"), "Sends a private message to a player. Params: /message <player_name> <content>", 0, "message", "msg", "whisper");
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
        if (args.length == 2) {
            AbstractPlayerEntity player = game.getWorld().getPlayer(args[0]);
            if (player != null) {
                sender.sendMessageTo(chat, new TextChatComponent(FormattingCode.GREEN + "Message sent!"));
                player.sendMessageTo(chat, new TextChatComponent(FormattingCode.ITALICS + sender.getChatColorFormat() + playerName + FormattingCode.RESET_COLOR + " sent you a message:" + FormattingCode.RESET_PROPS + "\n" + FormattingCode.GRAY + args[1]));
                return null;
            } else {
                return new TextChatComponent(FormattingCode.RED + "Couldn't find player with name " + args[0] + '!');
            }
        } else {
            return new TextChatComponent(FormattingCode.RED + "Specify a player and a message!");
        }
    }

    @Override
    public int getMaxArgumentAmount() {
        return 2;
    }
}
