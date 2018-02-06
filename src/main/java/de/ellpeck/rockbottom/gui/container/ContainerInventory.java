package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.GuiInventory;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class ContainerInventory extends ItemContainer{

    public ContainerInventory(EntityPlayer player){
        super(player, player.getInv());
        this.addPlayerInventory(player, 5, GuiInventory.PAGE_HEIGHT+5);
    }

    public static void doInvBasedConstruction(AbstractEntityPlayer player, IRecipe recipe, int amount){
        if(recipe.isKnown(player)){
            for(int a = 0; a < amount; a++){
                if(IRecipe.matchesInv(recipe, player.getInv())){
                    List<ItemInstance> usedInputs = new ArrayList<>();

                    for(IUseInfo input : recipe.getActualInputs(player.getInv())){
                        for(int i = 0; i < player.getInv().getSlotAmount(); i++){
                            ItemInstance inv = player.getInv().get(i);

                            if(inv != null && input.containsItem(inv) && inv.getAmount() >= input.getAmount()){
                                usedInputs.add(inv.copy().setAmount(input.getAmount()));
                                player.getInv().remove(i, input.getAmount());
                                break;
                            }
                        }
                    }

                    for(ItemInstance output : recipe.getActualOutputs(player.getInv(), usedInputs)){
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
