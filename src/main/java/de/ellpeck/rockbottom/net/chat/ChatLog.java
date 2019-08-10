package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.ChatMessageEvent;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.util.Counter;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.gui.GuiChat;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.net.chat.command.*;
import de.ellpeck.rockbottom.net.packet.toclient.PacketChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatLog implements IChatLog {

    private final List<ChatComponent> messages = new ArrayList<>();
    private final List<Counter> newMessageCounter = new ArrayList<>();
    private final List<String> lastInputs = new ArrayList<>();

    public static void initCommands() {
        new CommandHelp().register();
        new CommandStopServer().register();
        new CommandSpawnItem().register();
        new CommandSpawnEntity().register();
        new CommandTeleport().register();
        new CommandWhitelist().register();
        new CommandBlacklist().register();
        new CommandTime().register();
        new CommandEffect().register();
        new CommandItemList().register();
        new CommandMe().register();
        new CommandMessage().register();
        new CommandGamemode().register();
    }

    @Override
    public void displayMessage(ChatComponent message) {
        this.messages.add(0, message);

        if (!RockBottomAPI.getGame().isDedicatedServer()) {
            this.newMessageCounter.add(0, new Counter(400));
        }

        Logging.chatLogger.info(message.getUnformattedWithChildren());
    }

    @Override
    public void sendCommandSenderMessage(String message, ICommandSender sender) {
        if (RockBottomAPI.getNet().isServer()) {
            ChatMessageEvent event = new ChatMessageEvent(this, sender, message);
            if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
                message = event.message.trim();

                if (message.startsWith("/")) {
                    ChatComponent cmdFeedback;

                    String cmdName = message.substring(1).split(" ", 2)[0];
                    Command command = this.getCommand(cmdName);

                    if (command != null) {
                        if (sender.getCommandLevel() >= command.getLevel()) {
                            String[] args;
                            if (message.length() >= cmdName.length() + 2) {
                                args = message.substring(cmdName.length() + 2).split(" ", command.getMaxArgumentAmount());
                            } else {
                                args = new String[0];
                            }

                            cmdFeedback = command.execute(args, sender, sender.getName(), RockBottomAPI.getGame(), this);
                        } else {
                            cmdFeedback = new ChatComponentText(FormattingCode.RED + "You are not allowed to execute this command!");
                        }
                    } else {
                        cmdFeedback = new ChatComponentText(FormattingCode.RED + "Unknown command, use /help for a list of commands.");
                    }

                    if (cmdFeedback != null) {
                        this.sendMessageTo(sender, cmdFeedback);
                    }

                    Logging.chatLogger.info("Command sender " + sender.getName() + " with id " + sender.getUniqueId() + " executed command '/" + cmdName + "' with feedback '" + cmdFeedback + '\'');
                } else {
                    this.broadcastMessage(new ChatComponentText(sender.getChatColorFormat() + '[' + sender.getName() + "] &4" + message));
                }
            }
        }
    }

    @Override
    public Command getCommand(String name) {
        if (Util.isResourceName(name)) {
            return Registries.COMMAND_REGISTRY.get(new ResourceName(name));
        } else {
            for (Command command : Registries.COMMAND_REGISTRY.values()) {
                for (String s : command.getTriggers()) {
                    if (name.equals(s)) {
                        return command;
                    }
                }
            }
            return null;
        }
    }

    @Override
    public void sendMessageTo(ICommandSender sender, ChatComponent message) {
        sender.sendMessageTo(this, message);
    }

    @Override
    public void broadcastMessage(ChatComponent message) {
        this.displayMessage(message);

        if (RockBottomAPI.getNet().isServer()) {
            RockBottomAPI.getNet().sendToAllPlayers(RockBottomAPI.getGame().getWorld(), new PacketChatMessage(message));
        }
    }

    @Override
    public List<ChatComponent> getMessages() {
        return this.messages;
    }

    @Override
    public List<String> getLastInputs() {
        return this.lastInputs;
    }

    //TODO Make this get a uuid from online if one in the world isn't available
    @Override
    public UUID getPlayerIdFromString(String nameOrId) {
        try {
            return UUID.fromString(nameOrId);
        } catch (Exception e) {
            IWorld world = RockBottomAPI.getGame().getWorld();
            AbstractEntityPlayer player = world.getPlayer(nameOrId);

            if (player != null) {
                return player.getUniqueId();
            } else {
                return null;
            }
        }
    }

    @Override
    public List<String> getPlayerSuggestions() {
        List<String> suggestions = new ArrayList<>();

        IWorld world = RockBottomAPI.getGame().getWorld();
        for (AbstractEntityPlayer player : world.getAllPlayers()) {
            suggestions.add(player.getName());
            suggestions.add(player.getUniqueId().toString());
        }

        return suggestions;
    }

    @Override
    public void clear() {
        this.messages.clear();
        this.newMessageCounter.clear();
        this.lastInputs.clear();
    }

    public void drawNewMessages(RockBottom game, IAssetManager manager, IRenderer g) {
        if (!this.newMessageCounter.isEmpty()) {
            GuiChat.drawMessages(game, manager, g, this.messages, this.newMessageCounter.size(), 0, (int) g.getHeightInGui() / 2);
        }
    }

    public void updateNewMessages() {
        if (!this.newMessageCounter.isEmpty()) {
            for (int i = this.newMessageCounter.size() - 1; i >= 0; i--) {
                Counter counter = this.newMessageCounter.get(i);
                counter.add(-1);

                if (counter.get() <= 0) {
                    this.newMessageCounter.remove(i);
                }
            }
        }
    }
}
