package de.ellpeck.game.gui;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.component.ComponentButton;
import de.ellpeck.game.world.entity.player.EntityPlayer;

public class GuiDead extends Gui{

    public GuiDead(EntityPlayer player, int sizeX, int sizeY){
        super(player, sizeX, sizeY);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        this.components.add(new ComponentButton(this.guiLeft+this.sizeX/2, this.guiTop+this.sizeY/2+20, 20, 8, null, "Test text", "Test overlay"));
    }

    @Override
    public boolean doesPauseGame(){
        return false;
    }
}
