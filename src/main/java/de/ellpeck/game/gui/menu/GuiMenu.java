package de.ellpeck.game.gui.menu;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.gui.component.ComponentButton;
import de.ellpeck.game.world.entity.player.EntityPlayer;

public class GuiMenu extends Gui{

    public GuiMenu(EntityPlayer player){
        super(player, 100, 32);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, 0, this.guiLeft, this.guiTop, this.sizeX, 16, "Settings"));

        this.components.add(new ComponentButton(this, -1, this.guiLeft+10, this.guiTop+20, 80, 16, "Close Menu"));
    }

    @Override
    public boolean onButtonActivated(Game game, int button){
        if(button == -1){
            this.player.guiManager.closeGui();
            return true;
        }
        else if(button == 0){
            this.player.guiManager.openGui(new GuiSettings(this.player, this));
        }
        return false;
    }

}
