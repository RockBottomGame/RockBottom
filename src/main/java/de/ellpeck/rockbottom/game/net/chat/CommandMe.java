package de.ellpeck.rockbottom.game.net.chat;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.assets.font.FormattingCode;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;

public class CommandMe extends Command{

    public CommandMe(){
        super("me", "/me <message>", 0);
    }

    @Override
    public String execute(String[] args, EntityPlayer player, String playerName, IGameInstance game, AssetManager manager, ChatLog chat){
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
