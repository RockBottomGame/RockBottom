package de.ellpeck.game.gui;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.component.ComponentSlot;
import de.ellpeck.game.world.entity.player.EntityPlayer;

public class GuiInventory extends GuiContainer{

    public GuiInventory(EntityPlayer player){
        super(player, 158, 83);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        int x = 0;
        int y = 0;
        for(int i = 0; i < this.player.playerInventory.getSlotAmount(); i++){
            this.components.add(new ComponentSlot(this, this.player.playerInventory, i, this.guiLeft+x, this.guiTop+y));

            x += 20;

            if(i == 7 || i == 15 || i == 23){
                y += i == 7 ? 25 : 20;
                x = 0;
            }
        }
    }

    @Override
    public boolean doesPauseGame(){
        return false;
    }
}
