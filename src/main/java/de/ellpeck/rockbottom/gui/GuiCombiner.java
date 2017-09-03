package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentProgressBar;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityCombiner;

public class GuiCombiner extends GuiContainer{

    private final TileEntityCombiner tile;

    public GuiCombiner(AbstractEntityPlayer player, TileEntityCombiner tile){
        super(player, 198, 150);
        this.tile = tile;
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.components.add(new ComponentProgressBar(this, this.guiLeft+90, this.guiTop+15, 40, 8, PROGRESS_COLOR, false, this.tile:: getSmeltPercentage));
        this.components.add(new ComponentProgressBar(this, this.guiLeft+84, this.guiTop+30, 8, 18, FIRE_COLOR, true, this.tile:: getFuelPercentage));
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("separator");
    }
}
