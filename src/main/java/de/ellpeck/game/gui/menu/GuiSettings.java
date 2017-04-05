package de.ellpeck.game.gui.menu;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.gui.component.ComponentButton;
import de.ellpeck.game.world.entity.player.EntityPlayer;

public class GuiSettings extends Gui{

    public GuiSettings(EntityPlayer player, Gui parent){
        super(player, 100, 100, parent);
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, 0, this.guiLeft, this.guiTop, this.sizeX, 16, "Controls"));
        this.components.add(new ComponentButton(this, -1, this.guiLeft+10, this.guiTop+this.sizeY-16, 80, 16, "Back"));
    }


    @Override
    public boolean onButtonActivated(Game game, int button){
        if(button == -1){
            this.player.guiManager.openGui(this.parent);
            return true;
        }
        else if(button == 0){
            this.player.guiManager.openGui(new GuiKeybinds(this.player, this));
        }
        return false;
    }
}
