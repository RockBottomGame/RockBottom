package de.ellpeck.rockbottom.game.net.chat;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.game.item.ItemInstance;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;

import java.util.Arrays;

public class CommandSpawnItem extends Command{

    public CommandSpawnItem(){
        super("spawnitem", "/spawnitem <item id> [amount] [meta]", 10);
    }

    @Override
    public String execute(String[] args, EntityPlayer player, String playerName, RockBottom game, AssetManager manager, ChatLog chat){
        try{
            Item item = RockBottomAPI.ITEM_REGISTRY.get(args[0]);

            if(item != null){
                int amount = args.length < 2 ? 1 : Integer.parseInt(args[1]);
                int meta = args.length < 3 ? 0 : Integer.parseInt(args[2]);

                ItemInstance instance = new ItemInstance(item, amount, meta);
                player.inv.add(instance, false);
                return FormattingCode.GREEN+"Added "+amount+"x "+instance.getDisplayName()+"!";
            }
            else{
                return FormattingCode.RED+"Item with name "+args[0]+" not found!";
            }
        }
        catch(Exception e){
            return FormattingCode.RED+"Error formatting number for command args "+Arrays.toString(args)+"!";
        }
    }
}
