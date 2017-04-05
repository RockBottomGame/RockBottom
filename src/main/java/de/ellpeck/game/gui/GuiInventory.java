package de.ellpeck.game.gui;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.gui.component.ComponentButton;
import de.ellpeck.game.gui.component.ComponentSlot;
import de.ellpeck.game.item.Item;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.newdawn.slick.Input;

public class GuiInventory extends GuiContainer{

    public GuiInventory(EntityPlayer player){
        super(player, 158, 83);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        int x = 0;
        int y = 0;
        for(int i = 0; i < this.player.inv.getSlotAmount(); i++){
            this.components.add(new ComponentSlot(this, this.player.inv, i, this.guiLeft+x, this.guiTop+y));

            x += 20;

            if(i == 7 || i == 15 || i == 23){
                y += i == 7 ? 25 : 20;
                x = 0;
            }
        }

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-15, this.guiTop+this.sizeY+10, 30, 10, game.assetManager.localize("button.close")));
    }

    @Override
    public boolean onKeyboardAction(Game game, int button){
        if(button == Input.KEY_F){
            for(Item item : ContentRegistry.ITEM_REGISTRY.getUnmodifiable().values()){
                this.player.inv.add(new ItemInstance(item, 500), false);
            }
            return true;
        }
        else{
            return super.onKeyboardAction(game, button);
        }
    }

    @Override
    public boolean onButtonActivated(Game game, int button){
        if(button == -1){
            this.player.guiManager.closeGui();
            return true;
        }
        else{
            return super.onButtonActivated(game, button);
        }
    }

    @Override
    public boolean doesPauseGame(){
        return false;
    }
}
