package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class GuiInventory extends GuiContainer{

    public GuiInventory(AbstractEntityPlayer player){
        super(player, 135, 70);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        this.components.add(new ComponentFancyButton(this, -16, 0, 14, 14, () -> {
            game.getGuiManager().closeGui();
            this.player.openGuiContainer(new GuiCompendium(this.player), this.player.getInvContainer());
            return true;
        }, RockBottomAPI.createInternalRes("gui.construction.book_closed"), "Open the Compendium"));
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("inventory");
    }
}
