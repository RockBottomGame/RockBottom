package de.ellpeck.rockbottom.game.net.chat;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;

public class CommandMe extends Command{

    public CommandMe(){
        super("me", "/me <message>", 0);
    }

    @Override
    public String execute(String[] args, AbstractEntityPlayer player, String playerName, IGameInstance game, IAssetManager manager, IChatLog chat){
        if(args.length <= 0){
            return FormattingCode.RED+"Message is missing!";
        }
        else{
            String sentence = "";

            for(String s : args){
                sentence += " "+s;
            }

            chat.broadcastMessage(player.getChatColorFormat()+playerName+sentence);

            return null;
        }
    }
}
