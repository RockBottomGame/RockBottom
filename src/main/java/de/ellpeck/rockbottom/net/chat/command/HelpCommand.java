package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Arrays;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(ResourceName.intern("help"), "Shows a list of possible commands", 0, "help", "?");
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
        StringBuilder s = new StringBuilder(FormattingCode.GREEN + "All Commands: \n");

        for (Command command : Registries.COMMAND_REGISTRY.values()) {
            s.append(FormattingCode.ORANGE)
                    .append(Arrays.toString(command.getTriggers()))
                    .append(FormattingCode.RESET_COLOR)
                    .append(": ")
                    .append(FormattingCode.LIGHT_GRAY)
                    .append(command.getDescription())
                    .append(' ')
                    .append(FormattingCode.GRAY)
                    .append("(Level ")
                    .append(command.getLevel())
                    .append("+)\n");
        }

        return new TextChatComponent(s.toString());
    }
}
