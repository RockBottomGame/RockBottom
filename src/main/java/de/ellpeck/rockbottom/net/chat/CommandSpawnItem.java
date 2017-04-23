package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.font.FormattingCode;
import de.ellpeck.rockbottom.item.Item;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.Arrays;

public class CommandSpawnItem extends Command{

    public CommandSpawnItem(){
        super("spawnitem", "/spawnitem <item id> [amount] [meta]", 10);
    }

    @Override
    public String execute(String[] args, EntityPlayer player, RockBottom game, AssetManager manager, ChatLog chat){
        try{
            int id = Integer.parseInt(args[0]);
            Item item = ContentRegistry.ITEM_REGISTRY.get(id);

            if(item != null){
                int amount = args.length < 2 ? 1 : Integer.parseInt(args[1]);
                int meta = args.length < 3 ? 0 : Integer.parseInt(args[2]);

                ItemInstance instance = new ItemInstance(item, amount, meta);
                player.inv.add(instance, false);
                return FormattingCode.GREEN+"Added "+amount+"x "+manager.localize(item.getUnlocalizedName(instance))+"!";
            }
            else{
                return FormattingCode.RED+"Item with id "+id+" not found!";
            }
        }
        catch(Exception e){
            return FormattingCode.RED+"Error formatting number for command args "+Arrays.toString(args)+"!";
        }
    }
}
