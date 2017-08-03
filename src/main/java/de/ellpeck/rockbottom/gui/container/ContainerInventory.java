package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class ContainerInventory extends ItemContainer{

    public ContainerInventory(EntityPlayer player){
        super(player, player.getInv());
        this.addPlayerInventory(player, 0, 0);
    }

    public static void doInvBasedConstruction(AbstractEntityPlayer player, IRecipe recipe, int amount){
        for(int a = 0; a < amount; a++){
            if(IRecipe.matchesInv(recipe, player.getInv())){
                for(IUseInfo input : recipe.getInputs()){
                    for(int i = 0; i < player.getInv().getSlotAmount(); i++){
                        ItemInstance inv = player.getInv().get(i);

                        if(inv != null && input.containsItem(inv) && inv.getAmount() >= input.getAmount()){
                            player.getInv().remove(i, input.getAmount());
                            break;
                        }
                    }
                }

                for(ItemInstance output : recipe.getOutputs()){
                    ItemInstance left = player.getInv().addExistingFirst(output, false);
                    if(left != null){
                        EntityItem.spawn(player.world, left, player.x, player.y, 0F, 0F);
                    }
                }
            }
            else{
                break;
            }
        }
    }

    @Override
    public int getUnboundId(){
        return 0;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("inventory");
    }
}
