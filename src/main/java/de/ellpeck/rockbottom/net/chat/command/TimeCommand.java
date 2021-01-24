package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.net.packet.toclient.TimePacket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TimeCommand extends Command {

    public TimeCommand() {
        super(ResourceName.intern("time"), "Changes the world's time. Params: <set/advance> <amount>", 4);
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
        if (args.length >= 2) {
            IWorld world = sender.getWorld();

            if ("freeze".equals(args[0])) {
                boolean value;
                if ("on".equals(args[1])) {
                    value = true;
                } else if ("off".equals(args[1])) {
                    value = false;
                } else {
                    return new TextChatComponent(FormattingCode.RED + "Couldn't parse freeze value!");
                }
                world.setTimeFrozen(value);

                RockBottomAPI.getNet().sendToAllPlayersInWorld(world, new TimePacket(world.getCurrentTime(), world.getTotalTime(), world.isTimeFrozen()));
                return new TextChatComponent(FormattingCode.GREEN + "Set time freeze to " + value + '!');
            } else {
                try {
                    int amount = Math.abs(Integer.parseInt(args[1])) % Constants.TIME_PER_DAY;

                    if ("set".equals(args[0])) {
                        world.setCurrentTime(amount);
                    } else if ("advance".equals(args[0])) {
                        world.setCurrentTime(world.getCurrentTime() + amount);
                    } else {
                        return new TextChatComponent(FormattingCode.RED + "Specify your action!");
                    }

                    RockBottomAPI.getNet().sendToAllPlayersInWorld(world, new TimePacket(world.getCurrentTime(), world.getTotalTime(), world.isTimeFrozen()));
                    return new TextChatComponent(FormattingCode.GREEN + "Set time to " + world.getCurrentTime() + '!');
                } catch (NumberFormatException e) {
                    return new TextChatComponent(FormattingCode.RED + "Couldn't parse time!");
                }
            }
        } else {
            return new TextChatComponent(FormattingCode.RED + "Wrong number of arguments!");
        }
    }

    @Override
    public List<String> getAutocompleteSuggestions(String[] args, int argNumber, ICommandSender sender, IGameInstance game, IChatLog chat) {
        if (argNumber == 0) {
            return Arrays.asList("set", "advance", "freeze");
        } else if (argNumber == 1 && "freeze".equals(args[0])) {
            return Arrays.asList("on", "off");
        } else {
            return Collections.emptyList();
        }
    }
}
