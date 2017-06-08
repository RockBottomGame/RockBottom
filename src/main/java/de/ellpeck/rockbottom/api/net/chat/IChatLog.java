package de.ellpeck.rockbottom.api.net.chat;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;

import java.util.List;

public interface IChatLog{

    static void registerCommand(Command command){
        RockBottomAPI.COMMAND_REGISTRY.put(command.getName(), command);
    }

    void displayMessage(String message);

    void sendPlayerMessage(String message, AbstractEntityPlayer player, String playerName);

    void sendMessageToPlayer(AbstractEntityPlayer player, String message);

    void broadcastMessage(String message);

    List<String> getMessages();
}
