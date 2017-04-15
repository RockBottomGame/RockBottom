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

        this.addPlayerInventory(this.guiLeft, this.guiTop);
        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-15, this.guiTop+this.sizeY+10, 30, 10, game.assetManager.localize("button.close")));
    }

    @Override
    public boolean onKeyboardAction(Game game, int button, char character){
        if(button == Input.KEY_F){
            for(Item item : ContentRegistry.ITEM_REGISTRY.getUnmodifiable().values()){
                this.player.inv.add(new ItemInstance(item, item.getMaxAmount()), false);
            }
            return true;
        }
        else{
            return super.onKeyboardAction(game, button, character);
        }
    }

    @Override
    public boolean onButtonActivated(Game game, int button){
        if(button == -1){
            game.guiManager.closeGui();
            return true;
        }
        else{
            return super.onButtonActivated(game, button);
        }
    }
}
