package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class MeCommand extends Command {
    public MeCommand() {
        super(ResourceName.intern("me"), "/me enjoys this", 0);
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
        chat.broadcastMessage(new TextChatComponent(FormattingCode.ITALICS + sender.getChatColorFormat() + playerName + FormattingCode.RESET_COLOR + " " + args[0]));
        return null;
    }

    @Override
    public int getMaxArgumentAmount() {
        return 1;
    }
}
