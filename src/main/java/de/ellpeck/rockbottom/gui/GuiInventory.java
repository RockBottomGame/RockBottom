package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.gui.component.ComponentButton;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class GuiInventory extends GuiContainer{

    public GuiInventory(EntityPlayer player){
        super(player, 158, 83);
    }

    @Override
    public void initGui(RockBottom game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-15, this.guiTop+this.sizeY+10, 30, 10, game.assetManager.localize("button.close")));
    }

    @Override
    public boolean onButtonActivated(RockBottom game, int button){
        if(button == -1){
            game.guiManager.closeGui();
            return true;
        }
        else{
            return super.onButtonActivated(game, button);
        }
    }
}
