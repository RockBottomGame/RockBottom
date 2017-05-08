package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.inventory.IInventory;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ItemContainer{

    public final IInventory[] containedInventories;
    public final EntityPlayer player;
    private final List<ContainerSlot> slots = new ArrayList<>();

    public ItemContainer(EntityPlayer player, IInventory... containedInventories){
        this.player = player;
        this.containedInventories = containedInventories;
    }

    public ContainerSlot getSlot(int id){
        return this.slots.get(id);
    }

    public int getSlotAmount(){
        return this.slots.size();
    }

    public void addSlot(ContainerSlot slot){
        for(IInventory inv : this.containedInventories){
            if(inv == slot.inventory){
                this.slots.add(slot);
                return;
            }
        }

        Log.warn("Tried adding slot "+slot+" with inventory "+slot.inventory+" to container "+this+" that doesn't contain it!");
    }

    public int getIndexForInvSlot(IInventory inv, int id){
        for(int i = 0; i < this.slots.size(); i++){
            ContainerSlot slot = this.slots.get(i);
            if(slot.inventory == inv && slot.slot == id){
                return i;
            }
        }
        return -1;
    }

    protected void addSlotGrid(IInventory inventory, int start, int end, int xStart, int yStart, int width){
        int x = xStart;
        int y = yStart;
        for(int i = start; i < end; i++){
            this.addSlot(new ContainerSlot(inventory, i, x, y));

            x += 20;
            if((i+1)%width == 0){
                y += 20;
                x = xStart;
            }
        }
    }

    protected void addPlayerInventory(EntityPlayer player, int x, int y){
        this.addSlotGrid(player.inv, 0, 8, x, y, 8);
        this.addSlotGrid(player.inv, 8, player.inv.getSlotAmount(), x, y+25, 8);
    }

    public void onOpened(){

    }

    public void onClosed(){

    }

    public int getUnboundId(){
        return -1;
    }
}
