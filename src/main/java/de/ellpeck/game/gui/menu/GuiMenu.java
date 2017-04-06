package de.ellpeck.game.gui.menu;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.gui.component.ComponentButton;
import de.ellpeck.game.world.entity.player.EntityPlayer;

public class GuiMenu extends Gui{

    public GuiMenu(EntityPlayer player){
        super(player, 100, 100);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, 0, this.guiLeft, this.guiTop, this.sizeX, 16, game.assetManager.localize("button.settings")));

        this.components.add(new ComponentButton(this, -1, this.guiLeft+10, this.guiTop+this.sizeY-16, 80, 16, game.assetManager.localize("button.close")));
    }

    @Override
    public boolean onButtonActivated(Game game, int button){
        if(button == -1){
            game.guiManager.closeGui();
            return true;
        }
        else if(button == 0){
            game.guiManager.openGui(new GuiSettings(this.player, this));
        }
        return false;
    }

}
