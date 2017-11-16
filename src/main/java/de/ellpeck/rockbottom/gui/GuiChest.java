package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class GuiChest extends GuiContainer{

    public GuiChest(AbstractEntityPlayer player){
        super(player, 198, 138);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("chest");
    }
}
