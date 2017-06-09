package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;

public class GuiChest extends GuiContainer{

    public GuiChest(AbstractEntityPlayer player){
        super(player, 198, 150);
    }
}
