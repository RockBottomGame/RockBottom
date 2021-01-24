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
import java.util.UUID;

public class TeleportCommand extends Command {

    private static final int TELEPORT_LIMIT = 10000000;

    public TeleportCommand() {
        super(ResourceName.intern("teleport"), "Teleports a player. Params: <x> <y> [player]", 3, "tp", "tele", "teleport");
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
        AbstractPlayerEntity player;
        double x;
        double y;

        try {
            x = Math.max(-TELEPORT_LIMIT, Math.min(TELEPORT_LIMIT, Double.parseDouble(args[0])));
        } catch (Exception e) {
            return new TextChatComponent(FormattingCode.RED + "Couldn't parse x coordinate!");
        }

        try {
            y = Math.max(-TELEPORT_LIMIT, Math.min(TELEPORT_LIMIT, Double.parseDouble(args[1])));
        } catch (Exception e) {
            return new TextChatComponent(FormattingCode.RED + "Couldn't parse y coordinate!");
        }

        if (args.length > 2) {
            UUID id = chat.getPlayerIdFromString(args[2]);
            player = id != null ? game.getWorld().getPlayer(id) : null;

            if (player == null) {
                return new TextChatComponent(FormattingCode.RED + "Player " + args[2] + " not found!");
            }
        } else {
            if (sender instanceof AbstractPlayerEntity) {
                player = (AbstractPlayerEntity) sender;
            } else {
                return new TextChatComponent(FormattingCode.RED + "Only players can be teleported!");
            }
        }

        player.setPos(x, y);
        return new TextChatComponent(FormattingCode.GREEN + "Teleported player " + player.getName() + " to " + x + ", " + y);
    }

    @Override
    public List<String> getAutocompleteSuggestions(String[] args, int argNumber, ICommandSender sender, IGameInstance game, IChatLog chat) {
        if (argNumber == 2) {
            return chat.getPlayerSuggestions();
        } else {
            return Collections.emptyList();
        }
    }
}
