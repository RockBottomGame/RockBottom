package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;

import java.util.Arrays;

public class CommandSpawnItem extends Command{

    public CommandSpawnItem(){
        super("spawnitem", "/spawnitem <item id> [amount] [meta]", 10);
    }

    @Override
    public String execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat){
        try{
            if(sender instanceof AbstractEntityPlayer){
                Item item = RockBottomAPI.ITEM_REGISTRY.get(RockBottomAPI.createRes(args[0]));

                if(item != null){
                    int amount = args.length < 2 ? 1 : Integer.parseInt(args[1]);
                    int meta = args.length < 3 ? 0 : Integer.parseInt(args[2]);

                    ItemInstance instance = new ItemInstance(item, amount, meta);
                    ((AbstractEntityPlayer)sender).getInv().add(instance, false);
                    return FormattingCode.GREEN+"Added "+amount+"x "+instance.getDisplayName()+"!";
                }
                else{
                    return FormattingCode.RED+"Item with name "+args[0]+" not found!";
                }
            }
            else{
                return FormattingCode.RED+"Only players can execute this command!";
            }
        }
        catch(Exception e){
            return FormattingCode.RED+"Error formatting number for command args "+Arrays.toString(args)+"!";
        }
    }
}
