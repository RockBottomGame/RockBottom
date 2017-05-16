package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.font.FormattingCode;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class CommandMe extends Command{

    public CommandMe(){
        super("me", "/me <message>", 0);
    }

    @Override
    public String execute(String[] args, EntityPlayer player, String playerName, RockBottom game, AssetManager manager, ChatLog chat){
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
