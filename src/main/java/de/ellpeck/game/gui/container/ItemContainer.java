package de.ellpeck.game.gui.container;

import de.ellpeck.game.inventory.IInventory;
import de.ellpeck.game.world.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class ItemContainer{

    public final List<ContainerSlot> slots = new ArrayList<>();

    public void addSlot(ContainerSlot slot){
        this.slots.add(slot);
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
}
